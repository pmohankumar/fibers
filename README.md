# Fibers

Experimenting with [Quasar fibers](http://docs.paralleluniverse.co/quasar/) and their integration with [Hystrix](https://github.com/Netflix/Hystrix).

All programs (files ending with Test.java) can be run with these VM options 
`-javaagent:jar/quasar-core-0.7.8-jdk8.jar -Dco.paralleluniverse.fibers.detectRunawayFibers=true -Dco.paralleluniverse.fibers.verifyInstrumentation=true`