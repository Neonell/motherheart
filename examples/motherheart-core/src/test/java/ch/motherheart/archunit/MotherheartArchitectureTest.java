package ch.motherheart.archunit;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class MotherheartArchitectureTest {

    private static final String FIXTURES = "ch.motherheart.archunit.fixtures";

    private static JavaClasses classes(String fixture) {
        return new ClassFileImporter().importPackages(FIXTURES + "." + fixture);
    }

    private static MotherheartArchitecture architecture(String fixture) {
        return MotherheartArchitecture.of(FIXTURES + "." + fixture + ".library").shared("common");
    }

    @TestFactory
    @DisplayName("a well-formed Motherheart application respects every rule")
    Stream<DynamicTest> goodLibraryRespectsEveryRule() {
        JavaClasses classes = classes("good");
        return architecture("good").rules().stream()
                .map(rule -> DynamicTest.dynamicTest(rule.getDescription(),
                        () -> assertThatCode(() -> rule.check(classes)).doesNotThrowAnyException()));
    }

    @Test
    void goodLibraryRespectsTheCompositeRule() {
        assertThatCode(() -> architecture("good").all().check(classes("good"))).doesNotThrowAnyException();
    }

    @Nested
    @DisplayName("a violation is reported")
    class Violations {

        @Test
        void whenAClassHasNoLayer() {
            assertViolation("nolayer", MotherheartArchitecture::domainClassesLiveInALayer, "MemberHelper is in no layer");
        }

        @Test
        void whenAResourceUsesARepository() {
            assertViolation("resourceusesrepository", MotherheartArchitecture::domainLayering, "MemberRepository");
        }

        @Test
        void whenADtoKnowsAnEntity() {
            assertViolation("dtousesentity", MotherheartArchitecture::domainLayering, "MemberView");
        }

        @Test
        void whenAServiceTalksToTheDatabase() {
            assertViolation("servicetalkstodatabase", MotherheartArchitecture::onlyPersistenceTalksToTheDatabase,
                    "EntityManager");
        }

        @Test
        void whenADomainUsesTheRepositoryOfAnotherDomain() {
            assertViolation("crossdomainrepository", MotherheartArchitecture::domainsMeetOnlyThroughServices,
                    "domain 'loan' reaches into domain 'member'");
        }

        @Test
        void whenTwoDomainsShareAnEntity() {
            assertViolation("sharedentity", MotherheartArchitecture::entitiesBelongToOneDomain, "[loan, member]");
        }

        @Test
        void whenTwoDomainsDependOnEachOther() {
            assertViolation("cycle", MotherheartArchitecture::domainsAreFreeOfCycles, "Cycle detected");
        }

        @Test
        void whenAListOfEntitiesIsReturned() {
            assertViolation("listofentities", MotherheartArchitecture::listsNeverContainEntities,
                    "collection of entities Member");
        }

        @Test
        void whenAListOfEntitiesIsUsed() {
            assertViolation("callsfindall", MotherheartArchitecture::listsNeverContainEntities,
                    "MemberService calls MemberRepository.findAll(), which returns a collection of entities Member");
        }

        @Test
        void whenNativeSqlIsNotValidated() {
            assertViolation("nativesql", MotherheartArchitecture::queriesAreValidated, "native SQL");
        }

        @Test
        void whenAQueryIsBuiltAtRuntime() {
            assertViolation("runtimehql", MotherheartArchitecture::queriesAreValidated, "built at runtime");
        }

        @Test
        void whenJooqPlainSqlIsUsed() {
            assertViolation("jooqplainsql", MotherheartArchitecture::queriesAreValidated, "jOOQ plain SQL");
        }

        private void assertViolation(String fixture, Function<MotherheartArchitecture, ArchRule> rule, String expected) {
            String pkg = "bad." + fixture;
            assertThatThrownBy(() -> rule.apply(architecture(pkg)).check(classes(pkg)))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining(expected);
        }
    }
}
