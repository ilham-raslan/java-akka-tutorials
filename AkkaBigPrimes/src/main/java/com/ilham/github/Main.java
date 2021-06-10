package com.ilham.github;

import akka.actor.typed.ActorSystem;

public class Main {
//    public static void main(String[] args) {
//        ActorSystem<String> actorSystem = ActorSystem.create(FirstSimpleBehavior.create(), "FirstActorSystem");
//        actorSystem.tell("say hello");
//        actorSystem.tell("who are you");
//        actorSystem.tell("create a child");
//        actorSystem.tell("This is the third message");
//    }
    public static void main(String[] args) {
        ActorSystem<ManagerBehavior.Command> actorSystem = ActorSystem.create(ManagerBehavior.create(), "managerActor");
        actorSystem.tell(new ManagerBehavior.InstructionCommand("start"));
    }
}
