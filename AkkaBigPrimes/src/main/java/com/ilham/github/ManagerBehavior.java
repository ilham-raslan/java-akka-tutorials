package com.ilham.github;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManagerBehavior extends AbstractBehavior<ManagerBehavior.Command> {

    public interface Command extends Serializable {}

    public static class InstructionCommand implements Command {
        public static final long serialVersionUID = 1L;
        private String message;

        public InstructionCommand(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static class ResultCommand implements Command {
        public static final long serialVersionUID = 1L;
        private BigInteger prime;

        public ResultCommand(BigInteger prime) {
            this.prime = prime;
        }

        public BigInteger getPrime() {
            return this.prime;
        }
    }

    private ManagerBehavior(ActorContext<ManagerBehavior.Command> context) {
        super(context);
    }
    
    public static Behavior<ManagerBehavior.Command> create() {
        return Behaviors.setup(ManagerBehavior::new);
    }

    private SortedSet<BigInteger> primes = new TreeSet<BigInteger>();

    @Override
    public Receive<ManagerBehavior.Command> createReceive() {
        return this.newReceiveBuilder()
                .onMessage(InstructionCommand.class, command -> {
                    if (command.getMessage().equals("start")) {
                        for (int i = 0; i < 20; i++) {
                            ActorRef<WorkerBehavior.Command> workerActor = this.getContext().spawn(WorkerBehavior.create(), "workerActor_" + i);
                            workerActor.tell(new WorkerBehavior.Command("start", this.getContext().getSelf()));
                            workerActor.tell(new WorkerBehavior.Command("start", this.getContext().getSelf()));
                        }
                    }
                    return this;
                })
                .onMessage(ResultCommand.class, command -> {
                    this.primes.add(command.getPrime());
                    System.out.printf("I have received %d prime numbers%n", this.primes.size());
                    if (this.primes.size() == 20) {
                        this.primes.forEach(System.out::println);
                    }
                    return this;
                })
                .build();
    }
}
