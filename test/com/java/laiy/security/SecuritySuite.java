package com.java.laiy.security;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import com.java.laiy.view.ConsoleMenuViewTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    SecurityTest.class,
    ConsoleMenuViewTest.class
})
public class SecuritySuite {
    // This class remains empty,
    // it is used only as a holder for the above annotations
} 