package com.sample;

class Starter {

    public static void main(String[] args) {
        if (args.length == 2) {
            if ("host".equals(args[0])) {
                runHost();
            } else if ("connect".equals(args[0])) {
                runClient();
            }
            System.out.println("Address: " + args[1]);
        }
    }

    private static void runHost() {
        System.out.println("I am a host!");
    }

    private static void runClient() {
        System.out.println("I am a client!");
    }
}
