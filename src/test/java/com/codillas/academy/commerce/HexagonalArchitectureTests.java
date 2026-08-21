package com.codillas.academy.commerce;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
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
            ROOT + ".customers.domain",
            ROOT + ".customers.application.port.in",
            ROOT + ".customers.application.port.out",
            ROOT + ".customers.application.service",
            ROOT + ".customers.adapter.in.web",
            ROOT + ".customers.adapter.out.persistence",
            ROOT + ".catalog.domain",
            ROOT + ".catalog.application.port.in",
            ROOT + ".catalog.application.port.out",
            ROOT + ".catalog.application.service",
            ROOT + ".catalog.adapter.in.web",
            ROOT + ".catalog.adapter.out.persistence",
            ROOT + ".inventory.domain",
            ROOT + ".inventory.application.port.in",
            ROOT + ".inventory.application.port.out",
            ROOT + ".inventory.application.service",
            ROOT + ".inventory.adapter.in.web",
            ROOT + ".inventory.adapter.out.persistence",
            ROOT + ".orders.domain",
            ROOT + ".orders.application.port.in",
            ROOT + ".orders.application.port.out",
            ROOT + ".orders.application.service",
            ROOT + ".orders.adapter.in.web",
            ROOT + ".orders.adapter.out.persistence"
    };

    @ArchTest
    static final ArchRule MODULES_USE_ONLY_THE_STANDARD_PACKAGE_LAYOUT = classes()
            .that().resideInAnyPackage(BUSINESS_MODULES)
            .and().doNotHaveSimpleName("package-info")
            .should().resideInAnyPackage(ALLOWED_MODULE_PACKAGES)
            .because("every module separates domain, application ports and services, and directional adapters");

    @ArchTest
    static final ArchRule HEXAGONAL_DEPENDENCIES_POINT_INWARD = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("InboundPort").definedBy("..application.port.in..")
            .layer("OutboundPort").definedBy("..application.port.out..")
            .layer("Domain").definedBy("..domain..")
            .layer("ApplicationService").definedBy("..application.service..")
            .layer("InboundAdapter").definedBy("..adapter.in..")
            .layer("OutboundAdapter").definedBy("..adapter.out..")
            .whereLayer("InboundPort").mayOnlyBeAccessedByLayers(
                    "ApplicationService", "InboundAdapter", "OutboundAdapter")
            .whereLayer("OutboundPort").mayOnlyBeAccessedByLayers(
                    "ApplicationService", "OutboundAdapter")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers(
                    "ApplicationService", "OutboundPort", "OutboundAdapter")
            .whereLayer("ApplicationService").mayNotBeAccessedByAnyLayer()
            .whereLayer("InboundAdapter").mayNotBeAccessedByAnyLayer()
            .whereLayer("OutboundAdapter").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule DOMAINS_ARE_FRAMEWORK_FREE = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..application..",
                    "..adapter.."
            );

    @ArchTest
    static final ArchRule APPLICATION_PORTS_ARE_FRAMEWORK_AND_IMPLEMENTATION_FREE = noClasses()
            .that().resideInAPackage("..application.port..")
            .and().doNotHaveSimpleName("package-info")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..application.service..",
                    "..adapter.."
            );

    @ArchTest
    static final ArchRule INBOUND_PORTS_DO_NOT_EXPOSE_DOMAIN_OR_OUTBOUND_PORTS = noClasses()
            .that().resideInAPackage("..application.port.in..")
            .and().doNotHaveSimpleName("package-info")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..domain..",
                    "..application.port.out.."
            );

    @ArchTest
    static final ArchRule OUTBOUND_PORTS_ARE_INTERFACES = classes()
            .that().resideInAPackage("..application.port.out..")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule APPLICATION_SERVICES_DO_NOT_USE_ADAPTER_FRAMEWORKS = noClasses()
            .that().resideInAPackage("..application.service..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.http..",
                    "org.springframework.web..",
                    "org.springframework.data..",
                    "org.springframework.dao..",
                    "jakarta.persistence..",
                    "jakarta.validation.."
            );

    @ArchTest
    static final ArchRule PERSISTENCE_FRAMEWORKS_STAY_IN_PERSISTENCE_ADAPTERS = noClasses()
            .that().resideOutsideOfPackage("..adapter.out.persistence..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.data..",
                    "org.springframework.dao..",
                    "jakarta.persistence.."
            );

    @ArchTest
    static final ArchRule WEB_FRAMEWORKS_STAY_IN_WEB_ADAPTERS = noClasses()
            .that().resideOutsideOfPackage("..adapter.in.web..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework.http..",
                    "org.springframework.web..",
                    "jakarta.validation.."
            );

    @ArchTest
    static final ArchRule CONTROLLERS_USE_PORTS_INSTEAD_OF_APPLICATION_IMPLEMENTATIONS = noClasses()
            .that().areAnnotatedWith(RestController.class)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application.service..",
                    "..application.port.out..",
                    "..domain.."
            );

    @ArchTest
    static final ArchRule CONTROLLERS_INJECT_AN_INBOUND_PORT = classes()
            .that().areAnnotatedWith(RestController.class)
            .should(haveAFieldWhoseTypeIsAnInboundPort());

    @ArchTest
    static final ArchRule APPLICATION_SERVICES_IMPLEMENT_INBOUND_PORTS = classes()
            .that().areAnnotatedWith(Service.class)
            .should().resideInAPackage("..application.service..")
            .andShould(implementAnInterfaceIn(".application.port.in"));

    @ArchTest
    static final ArchRule PERSISTENCE_ADAPTERS_IMPLEMENT_OUTBOUND_PORTS = classes()
            .that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
            .should().resideInAPackage("..adapter.out.persistence..")
            .andShould(implementAnInterfaceIn(".application.port.out"));

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

    private static ArchCondition<JavaClass> haveAFieldWhoseTypeIsAnInboundPort() {
        return new ArchCondition<>("have a field whose type is an inbound port") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                var satisfied = item.getAllFields().stream()
                        .map(field -> field.getRawType())
                        .anyMatch(type -> type.isInterface()
                                && type.getPackageName().endsWith(".application.port.in"));
                events.add(new SimpleConditionEvent(
                        item,
                        satisfied,
                        item.getName() + " does not inject an inbound port"
                ));
            }
        };
    }
}
