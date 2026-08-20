package com.codillas.academy.commerce;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.codillas.academy.commerce",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTests {

    private static final String ROOT = "com.codillas.academy.commerce";

    private static final String[] BUSINESS_MODULES = {
            ROOT + ".customers..",
            ROOT + ".catalog..",
            ROOT + ".inventory..",
            ROOT + ".orders.."
    };

    private static final String[] ALLOWED_MODULE_PACKAGES = {
            ROOT + ".customers.api",
            ROOT + ".customers.domain",
            ROOT + ".customers.application",
            ROOT + ".customers.adapter.web",
            ROOT + ".customers.adapter.persistence",
            ROOT + ".catalog.api",
            ROOT + ".catalog.domain",
            ROOT + ".catalog.application",
            ROOT + ".catalog.adapter.web",
            ROOT + ".catalog.adapter.persistence",
            ROOT + ".inventory.api",
            ROOT + ".inventory.domain",
            ROOT + ".inventory.application",
            ROOT + ".inventory.adapter.web",
            ROOT + ".inventory.adapter.persistence",
            ROOT + ".orders.api",
            ROOT + ".orders.domain",
            ROOT + ".orders.application",
            ROOT + ".orders.adapter.web",
            ROOT + ".orders.adapter.persistence"
    };

    @ArchTest
    static final ArchRule MODULES_USE_ONLY_THE_STANDARD_PACKAGE_LAYOUT = classes()
            .that().resideInAnyPackage(BUSINESS_MODULES)
            .and().doNotHaveSimpleName("package-info")
            .should().resideInAnyPackage(ALLOWED_MODULE_PACKAGES)
            .because("every module has only api, domain, application, adapter.web, and adapter.persistence");

    @ArchTest
    static final ArchRule HEXAGONAL_DEPENDENCIES_POINT_INWARD = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("API").definedBy("..api..")
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Web").definedBy("..adapter.web..")
            .layer("Persistence").definedBy("..adapter.persistence..")
            .whereLayer("API").mayOnlyBeAccessedByLayers("Application", "Web")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Web", "Persistence")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Web", "Persistence")
            .whereLayer("Web").mayNotBeAccessedByAnyLayer()
            .whereLayer("Persistence").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule BUSINESS_MODULES_ARE_FREE_OF_CYCLES = slices()
            .matching(ROOT + ".(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule DOMAINS_ARE_FRAMEWORK_FREE = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..api..",
                    "..application..",
                    "..adapter.."
            );

    @ArchTest
    static final ArchRule PUBLIC_APIS_ARE_FRAMEWORK_AND_IMPLEMENTATION_FREE = noClasses()
            .that().resideInAPackage("..api..")
            .and().doNotHaveSimpleName("package-info")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..domain..",
                    "..application..",
                    "..adapter.."
            );

    @ArchTest
    static final ArchRule APPLICATIONS_DO_NOT_USE_DELIVERY_OR_PERSISTENCE_FRAMEWORKS = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.web..",
                    "org.springframework.data..",
                    "jakarta.persistence..",
                    "jakarta.validation.."
            );

    @ArchTest
    static final ArchRule PERSISTENCE_FRAMEWORKS_STAY_IN_PERSISTENCE_ADAPTERS = noClasses()
            .that().resideOutsideOfPackage("..adapter.persistence..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.data..",
                    "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule WEB_FRAMEWORKS_STAY_IN_WEB_ADAPTERS = noClasses()
            .that().resideOutsideOfPackage("..adapter.web..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.web..",
                    "jakarta.validation.."
            );

    @ArchTest
    static final ArchRule CONTROLLERS_USE_PORTS_INSTEAD_OF_APPLICATION_IMPLEMENTATIONS = noClasses()
            .that().areAnnotatedWith(RestController.class)
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..domain..");

    @ArchTest
    static final ArchRule CONTROLLERS_INJECT_AN_API_PORT = classes()
            .that().areAnnotatedWith(RestController.class)
            .should(haveAFieldWhoseTypeIsAnApiInterface());

    @ArchTest
    static final ArchRule APPLICATION_SERVICES_IMPLEMENT_API_PORTS = classes()
            .that().areAnnotatedWith(Service.class)
            .should(implementAnInterfaceIn(".api"));

    @ArchTest
    static final ArchRule PERSISTENCE_ADAPTERS_IMPLEMENT_APPLICATION_PORTS = classes()
            .that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
            .should(implementAnInterfaceIn(".application"));

    @ArchTest
    static final ArchRule REST_CONTROLLERS_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().resideInAPackage("..adapter.web..")
            .andShould().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule EXCEPTION_HANDLERS_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().areAnnotatedWith(RestControllerAdvice.class)
            .should().resideInAPackage("..adapter.web..")
            .andShould().haveSimpleNameEndingWith("ExceptionHandler");

    @ArchTest
    static final ArchRule APPLICATION_SERVICES_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().areAnnotatedWith(Service.class)
            .should().resideInAPackage("..application..")
            .andShould().haveSimpleNameEndingWith("ApplicationService");

    @ArchTest
    static final ArchRule REPOSITORY_PORTS_ARE_APPLICATION_INTERFACES = classes()
            .that().resideInAPackage("..application..")
            .and().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule USE_CASE_PORTS_ARE_API_INTERFACES = classes()
            .that().resideInAPackage(ROOT + "..")
            .and().haveSimpleNameEndingWith("UseCases")
            .should().resideInAPackage("..api..")
            .andShould().beInterfaces();

    @ArchTest
    static final ArchRule JPA_ADAPTERS_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
            .should().resideInAPackage("..adapter.persistence..")
            .andShould().haveSimpleNameEndingWith("RepositoryAdapter");

    @ArchTest
    static final ArchRule JPA_ENTITIES_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().areAnnotatedWith(Entity.class)
            .should().resideInAPackage("..adapter.persistence..")
            .andShould().haveSimpleNameEndingWith("JpaEntity");

    @ArchTest
    static final ArchRule SPRING_DATA_REPOSITORIES_HAVE_A_CONSISTENT_HOME_AND_NAME = classes()
            .that().resideInAPackage(ROOT + "..")
            .and().areAssignableTo(org.springframework.data.repository.Repository.class)
            .should().resideInAPackage("..adapter.persistence..")
            .andShould().beInterfaces()
            .andShould().haveSimpleNameStartingWith("SpringData");

    @ArchTest
    static final ArchRule CUSTOMER_INTERNALS_ARE_NOT_EXPOSED = moduleInternalsAreNotExposed("customers");

    @ArchTest
    static final ArchRule CATALOG_INTERNALS_ARE_NOT_EXPOSED = moduleInternalsAreNotExposed("catalog");

    @ArchTest
    static final ArchRule INVENTORY_INTERNALS_ARE_NOT_EXPOSED = moduleInternalsAreNotExposed("inventory");

    @ArchTest
    static final ArchRule ORDER_INTERNALS_ARE_NOT_EXPOSED = moduleInternalsAreNotExposed("orders");

    private static ArchRule moduleInternalsAreNotExposed(String module) {
        var moduleRoot = ROOT + "." + module;
        return noClasses()
                .that().resideOutsideOfPackage(moduleRoot + "..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        moduleRoot + ".domain..",
                        moduleRoot + ".application..",
                        moduleRoot + ".adapter.."
                )
                .because("other modules may depend only on " + module + "::api");
    }

    private static ArchCondition<JavaClass> implementAnInterfaceIn(String packageSuffix) {
        return new ArchCondition<>("implement an interface in *" + packageSuffix) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                var satisfied = item.getAllRawInterfaces().stream()
                        .anyMatch(type -> type.getPackageName().endsWith(packageSuffix));
                events.add(new SimpleConditionEvent(
                        item,
                        satisfied,
                        item.getName() + " does not implement an interface in *" + packageSuffix
                ));
            }
        };
    }

    private static ArchCondition<JavaClass> haveAFieldWhoseTypeIsAnApiInterface() {
        return new ArchCondition<>("have a field whose type is an API interface") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                var satisfied = item.getAllFields().stream()
                        .map(field -> field.getRawType())
                        .anyMatch(type -> type.isInterface() && type.getPackageName().endsWith(".api"));
                events.add(new SimpleConditionEvent(
                        item,
                        satisfied,
                        item.getName() + " does not inject an API interface"
                ));
            }
        };
    }
}
