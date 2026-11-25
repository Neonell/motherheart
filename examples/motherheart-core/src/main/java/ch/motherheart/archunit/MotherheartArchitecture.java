package ch.motherheart.archunit;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.AccessTarget.MethodCallTarget;
import com.tngtech.archunit.core.domain.Dependency;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * The Motherheart manifest, executable.
 *
 * <p>A Motherheart application is a root package where every sub-package is a <em>domain</em>.
 * Inside a domain, the package tells the layer. Class names are free: a name is just a name.
 *
 * <pre>
 *   &lt;domain&gt;.api          REST entry points            → may only use its own service
 *   &lt;domain&gt;.service      the only door into a domain  → may use its own persistence,
 *                                                         and other domains' service / dto
 *   &lt;domain&gt;.persistence  repositories, queries, entities (the only place that talks to the database)
 *   &lt;domain&gt;.dto          records crossing the layers
 * </pre>
 *
 * <p>Entities are recognised by what they are ({@code @Entity}, or a jOOQ record via {@link #entities}).
 * Lists are always DTOs; a single object may be an entity or a DTO.
 *
 * <pre>{@code
 * MotherheartArchitecture.of("com.acme.shop")
 *     .heart("com.acme.heart")     // generated schema (e.g. jOOQ), optional
 *     .shared("common")            // shared kernel sub-packages, optional
 *     .rules();
 * }</pre>
 */
public final class MotherheartArchitecture {

    private static final String JPA_ENTITY = "jakarta.persistence.Entity";
    private static final String API = "api";
    private static final String SERVICE = "service";
    private static final String PERSISTENCE = "persistence";
    private static final String DTO = "dto";
    private static final Set<String> LAYERS = Set.of(API, SERVICE, PERSISTENCE, DTO);
    private static final Set<String> GENERATED_METAMODELS = Set.of(
            "jakarta.persistence.metamodel.StaticMetamodel",
            "jakarta.data.metamodel.StaticMetamodel");

    /** What only the persistence layer may hold. */
    private static final Set<String> DATABASE_GATEWAYS = Set.of(
            "jakarta.persistence.EntityManager",
            "org.hibernate.SharedSessionContract",
            "org.jooq.DSLContext",
            "java.sql.Connection",
            "javax.sql.DataSource");

    /** Persistence frameworks a REST resource or a DTO should not even know about. */
    private static final List<String> PERSISTENCE_PACKAGES = List.of(
            "jakarta.persistence", "org.hibernate", "org.jooq", "jakarta.data", "java.sql", "javax.sql");

    private static final Set<String> ORM_NATIVE_SQL = Set.of("createNativeQuery", "createNativeMutationQuery");
    private static final Set<String> ORM_RUNTIME_QUERY = Set.of(
            "createQuery", "createSelectionQuery", "createMutationQuery", "createNamedQuery");
    private static final Set<String> JOOQ_PLAIN_SQL = Set.of(
            "field", "table", "condition", "sql", "query", "resultQuery", "keyword",
            "fetch", "fetchOne", "fetchSingle", "fetchOptional", "fetchAny", "fetchValue", "fetchOptionalValue",
            "fetchValues", "fetchLazy", "fetchStream", "fetchMany", "fetchExists", "fetchCount", "execute");

    private final String root;
    private String heart;
    private final Set<String> shared = new LinkedHashSet<>();
    private DescribedPredicate<? super JavaClass> entities = annotatedWith(JPA_ENTITY).as("are entities");

    private MotherheartArchitecture(String root) {
        this.root = Objects.requireNonNull(root, "root package");
    }

    /** @param rootPackage the package whose direct sub-packages are the domains */
    public static MotherheartArchitecture of(String rootPackage) {
        return new MotherheartArchitecture(rootPackage);
    }

    /**
     * The package holding a generated database schema (jOOQ tables, records...). It belongs to no
     * domain: it is the heart, and every domain's persistence may read it.
     */
    public MotherheartArchitecture heart(String heartPackage) {
        this.heart = heartPackage;
        return this;
    }

    /** Sub-packages of the root that are a shared kernel (exception mapping, configuration...). */
    public MotherheartArchitecture shared(String... subPackages) {
        shared.addAll(List.of(subPackages));
        return this;
    }

    /** What counts as an entity. Defaults to {@code @jakarta.persistence.Entity}. */
    public MotherheartArchitecture entities(DescribedPredicate<? super JavaClass> entityPredicate) {
        this.entities = Objects.requireNonNull(entityPredicate, "entities");
        return this;
    }

    /** Every Motherheart rule. */
    public List<ArchRule> rules() {
        return List.of(
                domainClassesLiveInALayer(),
                domainLayering(),
                onlyPersistenceTalksToTheDatabase(),
                domainsMeetOnlyThroughServices(),
                entitiesBelongToOneDomain(),
                domainsAreFreeOfCycles(),
                listsNeverContainEntities(),
                queriesAreValidated());
    }

    /** Every Motherheart rule as one rule. */
    public ArchRule all() {
        return CompositeArchRule.of(rules()).allowEmptyShould(true);
    }

    // ---------------------------------------------------------------- rules

    /** Every class of a domain lives in api, service, persistence or dto: the package tells the layer. */
    public ArchRule domainClassesLiveInALayer() {
        return classes().that(inDomain())
                .should(new ArchCondition<>("live in one of the layers " + new TreeSet<>(LAYERS)) {
                    @Override
                    public void check(JavaClass javaClass, ConditionEvents events) {
                        if (layerOf(javaClass).filter(LAYERS::contains).isEmpty()) {
                            events.add(SimpleConditionEvent.violated(javaClass,
                                    "%s is in no layer; move it to %s.<api|service|persistence|dto>"
                                            .formatted(javaClass.getName(), root + "." + domainOf(javaClass).orElseThrow())));
                        }
                    }
                })
                .because("a reader must know the layer of a class without opening it (Motherheart §4)")
                .allowEmptyShould(true);
    }

    /** api → service → persistence → entity → heart. DTOs never touch entities. */
    public ArchRule domainLayering() {
        return layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .withOptionalLayers(true)
                .layer("Api").definedBy(layer(API))
                .layer("Service").definedBy(layer(SERVICE))
                .layer("Persistence").definedBy(persistence())
                .layer("Entity").definedBy(entity())
                .layer("Heart").definedBy(heartTables())
                .layer("Dto").definedBy(layer(DTO))
                .whereLayer("Api").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Api", "Service")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Persistence")
                .whereLayer("Entity").mayOnlyBeAccessedByLayers("Service", "Persistence", "Entity", "Heart")
                .whereLayer("Heart").mayOnlyBeAccessedByLayers("Persistence", "Entity", "Heart")
                .because("a domain is layered api → service → persistence; "
                        + "entities and the schema never leak towards the API (Motherheart §4)")
                .allowEmptyShould(true);
    }

    /** Only the persistence layer holds an EntityManager / DSLContext; api and dto ignore persistence entirely. */
    public ArchRule onlyPersistenceTalksToTheDatabase() {
        return classes().that(layer(API).or(layer(SERVICE)).or(layer(DTO)))
                .should(new ArchCondition<>("not talk to the database directly") {
                    @Override
                    public void check(JavaClass origin, ConditionEvents events) {
                        boolean mustIgnorePersistence = !layer(SERVICE).test(origin);
                        for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                            JavaClass target = dependency.getTargetClass().getBaseComponentType();
                            if (isAssignableToAny(target, DATABASE_GATEWAYS)) {
                                events.add(SimpleConditionEvent.violated(dependency,
                                        "only the persistence layer talks to the database: " + dependency.getDescription()));
                            } else if (mustIgnorePersistence && inAnyPackage(target, PERSISTENCE_PACKAGES)) {
                                events.add(SimpleConditionEvent.violated(dependency,
                                        "api and dto must not know the persistence technology: "
                                                + dependency.getDescription()));
                            }
                        }
                    }
                })
                .because("responsibility for executed SQL must stay visible in one place (Motherheart §7)")
                .allowEmptyShould(true);
    }

    /** The only bridge between two domains is a service calling another domain's service (or using its DTOs). */
    public ArchRule domainsMeetOnlyThroughServices() {
        return classes().that(inRoot())
                .should(new ArchCondition<>("reach other domains only from the service layer, towards their service or dto layer") {
                    @Override
                    public void check(JavaClass origin, ConditionEvents events) {
                        Optional<String> from = domainOf(origin);
                        if (from.isEmpty()) {
                            return;
                        }
                        for (Dependency dependency : origin.getDirectDependenciesFromSelf()) {
                            JavaClass target = dependency.getTargetClass().getBaseComponentType();
                            Optional<String> to = domainOf(target);
                            if (to.isEmpty() || to.equals(from)) {
                                continue;
                            }
                            boolean allowed = layer(SERVICE).test(origin)
                                    && (layer(SERVICE).test(target) || layer(DTO).test(target));
                            if (!allowed) {
                                events.add(SimpleConditionEvent.violated(dependency,
                                        "domain '%s' reaches into domain '%s' without going through a service: %s"
                                                .formatted(from.get(), to.get(), dependency.getDescription())));
                            }
                        }
                    }
                })
                .because("DTOs, repositories and APIs stay isolated within their domain boundary (Motherheart §4)")
                .allowEmptyShould(true);
    }

    /** An entity (or jOOQ record) is used by one domain only. Other domains refer to it by id. */
    public ArchRule entitiesBelongToOneDomain() {
        return classes().that(entity())
                .should(new ArchCondition<>("be used by exactly one domain") {
                    @Override
                    public void check(JavaClass entity, ConditionEvents events) {
                        Set<String> domains = new TreeSet<>();
                        domainOf(entity).ifPresent(domains::add);
                        for (Dependency dependency : entity.getDirectDependenciesToSelf()) {
                            domainOf(dependency.getOriginClass()).ifPresent(domains::add);
                        }
                        if (domains.size() > 1) {
                            events.add(SimpleConditionEvent.violated(entity,
                                    "entity %s is shared by the domains %s; reference it by id instead"
                                            .formatted(entity.getName(), domains)));
                        }
                    }
                })
                .because("no hidden joins: a foreign key is an id, not an object graph (Motherheart rule 5)")
                .allowEmptyShould(true);
    }

    /** Domains (and the shared kernel) never depend on each other in a cycle. */
    public ArchRule domainsAreFreeOfCycles() {
        return slices().matching(root + ".(*)..")
                .should().beFreeOfCycles()
                .because("a domain is a protected environment with a clear owner (Motherheart §4)")
                .allowEmptyShould(true);
    }

    /**
     * If it is a list, it is DTOs. No written method returns a List / Stream / Page / array of entities,
     * and nobody calls one: a framework may offer {@code findAll()}, it must not be used.
     * A single object may be an entity or a DTO. Generated code ({@code *_}) is not checked, its callers are.
     */
    public ArchRule listsNeverContainEntities() {
        return classes().that(inRoot()).and(DescribedPredicate.describe("are not generated", c -> !isGenerated(c)))
                .should(new ArchCondition<>("neither return nor use collections of entities") {
                    @Override
                    public void check(JavaClass origin, ConditionEvents events) {
                        for (JavaMethod method : origin.getMethods()) {
                            entityCollectionReturnedBy(method).ifPresent(type -> events.add(SimpleConditionEvent.violated(method,
                                    "%s returns a collection of entities %s; return a DTO projection"
                                            .formatted(method.getFullName(), type.getSimpleName()))));
                        }
                        for (JavaMethodCall call : origin.getMethodCallsFromSelf()) {
                            entityCollectionReturnedBy(call).ifPresent(type -> events.add(SimpleConditionEvent.violated(call,
                                    "%s calls %s.%s(), which returns a collection of entities %s; map the list to DTOs"
                                            .formatted(origin.getSimpleName(), call.getTarget().getOwner().getSimpleName(),
                                                    call.getName(), type.getSimpleName()))));
                        }
                    }
                })
                .because("a list or a collection is always mapped to DTOs: do not load what you do not need (Motherheart rules 2 and 4)")
                .allowEmptyShould(true);
    }

    /** As close to SQL as possible, but every query is validated at build time. */
    public ArchRule queriesAreValidated() {
        return classes().that(inRoot())
                .should(new ArchCondition<>("only run queries validated at build time") {
                    @Override
                    public void check(JavaClass origin, ConditionEvents events) {
                        for (JavaMethod method : origin.getMethods()) {
                            if (method.isAnnotatedWith("org.hibernate.annotations.processing.SQL")) {
                                events.add(SimpleConditionEvent.violated(method,
                                        method.getFullName() + " is a native @SQL query: nothing validates it at build time"));
                            }
                        }
                        for (JavaMethodCall call : origin.getMethodCallsFromSelf()) {
                            unvalidatedQuery(origin, call).ifPresent(reason ->
                                    events.add(SimpleConditionEvent.violated(call, reason + ": " + call.getDescription())));
                        }
                    }
                })
                .because("stay close to SQL, but never trust a query nobody validated (Motherheart §2)")
                .allowEmptyShould(true);
    }

    // ---------------------------------------------------------------- helpers

    private static Optional<String> unvalidatedQuery(JavaClass origin, JavaMethodCall call) {
        MethodCallTarget target = call.getTarget();
        JavaClass owner = target.getOwner();
        String name = target.getName();
        List<JavaClass> parameters = target.getRawParameterTypes();
        boolean textFirst = !parameters.isEmpty()
                && (parameters.get(0).getName().equals(String.class.getName())
                || parameters.get(0).getName().equals("org.jooq.SQL"));

        boolean orm = isAssignableToAny(owner, Set.of("jakarta.persistence.EntityManager", "org.hibernate.SharedSessionContract"));
        if (orm && ORM_NATIVE_SQL.contains(name)) {
            return Optional.of("native SQL string, unknown to the build");
        }
        if (orm && !isGenerated(origin) && ORM_RUNTIME_QUERY.contains(name) && textFirst) {
            return Optional.of("query string built at runtime; declare it with @Query / @Find so it is validated");
        }
        boolean jooq = owner.getName().equals("org.jooq.impl.DSL") || isAssignableToAny(owner, Set.of("org.jooq.DSLContext"));
        boolean plainSqlApi = target.resolveMember().map(m -> m.isAnnotatedWith("org.jooq.PlainSQL")).orElse(false);
        if (jooq && (plainSqlApi || (JOOQ_PLAIN_SQL.contains(name) && textFirst))) {
            return Optional.of("jOOQ plain SQL string, not checked against the generated schema");
        }
        return Optional.empty();
    }

    /** The entity type, if the method returns a container (list, stream, page, array, map) of entities. */
    private Optional<JavaClass> entityCollectionReturnedBy(JavaMethod method) {
        JavaClass raw = method.getRawReturnType();
        if (!isContainer(raw)) {
            return Optional.empty();
        }
        return method.getReturnType().getAllInvolvedRawTypes().stream()
                .map(JavaClass::getBaseComponentType)
                .filter(type -> !type.equals(raw) && entity().test(type))
                .findFirst();
    }

    /**
     * The entity type, if the called method returns a container of entities. An inherited framework method
     * ({@code BasicRepository.findAll()} returns {@code Stream<T>}) is generic, so the concrete type is read
     * from the generated implementation of the called repository, which declares {@code Stream<Customer>}.
     */
    private Optional<JavaClass> entityCollectionReturnedBy(JavaMethodCall call) {
        MethodCallTarget target = call.getTarget();
        String[] parameters = target.getRawParameterTypes().stream().map(JavaClass::getName).toArray(String[]::new);
        return Stream.concat(
                        target.resolveMember().stream(),
                        target.getOwner().getSubclasses().stream()
                                .filter(c -> inPackage(c, root) && isGenerated(c))
                                .flatMap(c -> c.tryGetMethod(target.getName(), parameters).stream()))
                .map(this::entityCollectionReturnedBy)
                .flatMap(Optional::stream)
                .findFirst();
    }

    /** The domain of a class: first sub-package below the root, unless it is a shared kernel. */
    Optional<String> domainOf(JavaClass javaClass) {
        String pkg = javaClass.getBaseComponentType().getPackageName();
        if (!pkg.startsWith(root + ".")) {
            return Optional.empty();
        }
        String domain = pkg.substring(root.length() + 1).split("\\.")[0];
        return shared.contains(domain) ? Optional.empty() : Optional.of(domain);
    }

    private DescribedPredicate<JavaClass> inRoot() {
        return DescribedPredicate.describe("are in " + root, c -> inPackage(c, root));
    }

    private DescribedPredicate<JavaClass> inDomain() {
        return DescribedPredicate.describe("belong to a domain of " + root, c -> domainOf(c).isPresent());
    }

    /** The layer of a class: second sub-package below the root ({@code <root>.<domain>.<layer>}). */
    Optional<String> layerOf(JavaClass javaClass) {
        if (domainOf(javaClass).isEmpty()) {
            return Optional.empty();
        }
        String[] parts = javaClass.getBaseComponentType().getPackageName().substring(root.length() + 1).split("\\.");
        return parts.length > 1 ? Optional.of(parts[1]) : Optional.empty();
    }

    /** Classes of the given layer, entities excepted (they have their own layer). */
    private DescribedPredicate<JavaClass> layer(String layer) {
        return DescribedPredicate.describe("are in the " + layer + " layer",
                c -> !entity().test(c) && layerOf(c).filter(layer::equals).isPresent());
    }

    private DescribedPredicate<JavaClass> persistence() {
        return DescribedPredicate.describe("are persistence (repositories, queries, generated metamodel)",
                c -> layer(PERSISTENCE).test(c) || GENERATED_METAMODELS.stream().anyMatch(c::isAnnotatedWith));
    }

    @SuppressWarnings("unchecked")
    private DescribedPredicate<JavaClass> entity() {
        return (DescribedPredicate<JavaClass>) entities;
    }

    private DescribedPredicate<JavaClass> heartTables() {
        return DescribedPredicate.describe("are the heart (generated schema)",
                c -> heart != null && inPackage(c, heart) && !entity().test(c));
    }

    /** {@code true} for implementations generated by an annotation processor ({@code CustomerRepository_}). */
    private static boolean isGenerated(JavaClass javaClass) {
        JavaClass current = javaClass.getBaseComponentType();
        while (current.getEnclosingClass().isPresent()) {
            current = current.getEnclosingClass().get();
        }
        return current.getSimpleName().endsWith("_");
    }

    private static boolean inPackage(JavaClass javaClass, String pkg) {
        String name = javaClass.getBaseComponentType().getPackageName();
        return name.equals(pkg) || name.startsWith(pkg + ".");
    }

    private static boolean inAnyPackage(JavaClass javaClass, List<String> packages) {
        return packages.stream().anyMatch(pkg -> inPackage(javaClass, pkg));
    }

    private static boolean isAssignableToAny(JavaClass javaClass, Set<String> typeNames) {
        return typeNames.stream().anyMatch(name -> javaClass.getName().equals(name) || javaClass.isAssignableTo(name));
    }

    private static boolean isContainer(JavaClass raw) {
        return raw.isArray()
                || raw.isAssignableTo("java.lang.Iterable")
                || raw.isAssignableTo("java.util.Map")
                || raw.isAssignableTo("java.util.stream.BaseStream")
                || raw.isAssignableTo("jakarta.data.page.Page");
    }
}
