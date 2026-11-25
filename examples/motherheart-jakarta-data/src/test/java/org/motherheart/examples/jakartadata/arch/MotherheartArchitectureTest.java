package org.motherheart.examples.jakartadata.arch;

import org.motherheart.archunit.MotherheartArchitecture;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/** The manifest, checked on every build. */
@AnalyzeClasses(packages = "org.motherheart.examples.jakartadata", importOptions = ImportOption.DoNotIncludeTests.class)
class MotherheartArchitectureTest {

    private static final MotherheartArchitecture SHOP =
            MotherheartArchitecture.of("org.motherheart.examples.jakartadata.shop").shared("common");

    @ArchTest
    static final ArchRule domain_classes_live_in_a_layer = SHOP.domainClassesLiveInALayer();

    @ArchTest
    static final ArchRule domain_layering = SHOP.domainLayering();

    @ArchTest
    static final ArchRule only_persistence_talks_to_the_database = SHOP.onlyPersistenceTalksToTheDatabase();

    @ArchTest
    static final ArchRule domains_meet_only_through_services = SHOP.domainsMeetOnlyThroughServices();

    @ArchTest
    static final ArchRule entities_belong_to_one_domain = SHOP.entitiesBelongToOneDomain();

    @ArchTest
    static final ArchRule domains_are_free_of_cycles = SHOP.domainsAreFreeOfCycles();

    @ArchTest
    static final ArchRule lists_never_contain_entities = SHOP.listsNeverContainEntities();

    @ArchTest
    static final ArchRule queries_are_validated = SHOP.queriesAreValidated();
}
