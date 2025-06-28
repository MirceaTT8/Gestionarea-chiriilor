package com.immobile.real_estate_backend;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.immobile.real_estate_backend",
        importOptions = {ImportOption.DoNotIncludeTests.class})
public class LayeredArchitectureTest {

    @ArchTest
    static void controllers_should_not_access_repositories(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().accessClassesThat().resideInAPackage("..repository..")
                .because("Controllers must not access repositories directly")
                .check(classes);
    }

    @ArchTest
    static void services_should_only_be_accessed_by_controllers_services_or_infrastructure(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..service..")
                .should().onlyBeAccessed().byAnyPackage(
                        "..controller..",
                        "..service..",
                        "..config..",        // ✅ allow JWT filters
                        "..scheduler..")     // ✅ allow background jobs
                .because("Services should only be accessed by controllers, other services, or infrastructure layers")
                .check(classes);
    }


    @ArchTest
    static void repositories_should_only_be_accessed_by_services(JavaClasses classes) {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..repository..")
                .should().onlyBeAccessed().byAnyPackage(
                        "..service..",
                        "..model.converter..",
                        "..scheduler.."
                )
                .because("Repositories should only be accessed by services, converters, or infrastructure")
                .check(classes);
    }

    @ArchTest
    static void controllers_should_not_depend_on_other_controllers(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..controller..")
                .because("Controllers should not directly depend on other controllers")
                .check(classes);
    }

    @ArchTest
    static void entities_should_not_be_used_in_controllers(JavaClasses classes) {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should().dependOnClassesThat().resideInAPackage("..model.entity..")
                .because("Controllers should not use entities directly, use DTOs instead")
                .check(classes);
    }

}

