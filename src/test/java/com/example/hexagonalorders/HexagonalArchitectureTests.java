package com.example.hexagonalorders;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(
        packages = "com.example.hexagonalorders.order",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTests {

    @ArchTest
    static final ArchRule domain_is_framework_free = noClasses()
            .that().resideInAPackage("..internal.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..",
                    "jakarta..",
                    "..internal.application..",
                    "..internal.port..",
                    "..internal.web..",
                    "..internal.persistence.."
            );

    @ArchTest
    static final ArchRule core_does_not_depend_on_adapters = noClasses()
            .that().resideInAnyPackage(
                    "..internal.application..",
                    "..internal.domain..",
                    "..internal.port.."
            )
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..internal.web..",
                    "..internal.persistence.."
            );
}
