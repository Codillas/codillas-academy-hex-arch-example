package com.codillas.academy.commerce;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.codillas.academy.commerce",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTests {

    @ArchTest
    static final ArchRule DOMAINS_ARE_FRAMEWORK_FREE = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..application..",
                    "..adapter..",
                    "..api.."
            );

    @ArchTest
    static final ArchRule CORES_DO_NOT_DEPEND_ON_ADAPTERS = noClasses()
            .that().resideInAnyPackage("..domain..", "..application..", "..api..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..")
            .because("dependencies must point inward");

    @ArchTest
    static final ArchRule PUBLIC_APIS_ARE_FRAMEWORK_FREE = noClasses()
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
    static final ArchRule INBOUND_ADAPTERS_DO_NOT_REACH_OUTBOUND_ADAPTERS = noClasses()
            .that().resideInAPackage("..adapter.inbound..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.outbound..");
}
