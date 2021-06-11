package com.ilham.github;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.Duration;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManagerBehavior extends AbstractBehavior<ManagerBehavior.Command> {

    public interface Command extends Serializable {}

    public static class InstructionCommand implements Command {
        public static final long serialVersionUID = 1L;
        private String message;
        private ActorRef<SortedSet<BigInteger>> sender;

        public InstructionCommand(String message, ActorRef<SortedSet<BigInteger>> sender) {
            this.message = message;
            this.sender = sender;
        }

        public String getMessage() {
            return this.message;
        }

        public ActorRef<SortedSet<BigInteger>> getSender() {
            return sender;
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

    private class NoResponseReceivedCommand implements Command {
        public static final long serialVersionUID = 1L;
        private ActorRef<WorkerBehavior.Command> worker;

        private NoResponseReceivedCommand(ActorRef<WorkerBehavior.Command> worker) {
            this.worker = worker;
        }

        public ActorRef<WorkerBehavior.Command> getWorker() {
            return worker;
        }
    }

    private ManagerBehavior(ActorContext<ManagerBehavior.Command> context) {
        super(context);
    }
    
    public static Behavior<ManagerBehavior.Command> create() {
        return Behaviors.setup(ManagerBehavior::new);
    }

    private SortedSet<BigInteger> primes = new TreeSet<BigInteger>();

    private ActorRef<SortedSet<BigInteger>> sender;

    @Override
    public Receive<ManagerBehavior.Command> createReceive() {
        return this.newReceiveBuilder()
                .onMessage(InstructionCommand.class, command -> {
                    if (command.getMessage().equals("start")) {
                        this.sender = command.getSender();
                        for (int i = 0; i < 20; i++) {
                            ActorRef<WorkerBehavior.Command> worker = this.getContext().spawn(WorkerBehavior.create(), "workerActor_" + i);
                            askWorkerForAPrime(worker);
                        }
                    }
                    return Behaviors.same();
                })
                .onMessage(ResultCommand.class, command -> {
                    this.primes.add(command.getPrime());
                    System.out.printf("I have received %d prime numbers%n", this.primes.size());
                    if (primes.size() == 20) {
                        this.sender.tell(primes);
                    }
                    return Behaviors.same();
                })
                .onMessage(NoResponseReceivedCommand.class, command -> {
                    System.out.printf("Retrying with worker %s%n", command.getWorker().path());
                    askWorkerForAPrime(command.getWorker());
                    return Behaviors.same();
                })
                .build();
    }

    private void askWorkerForAPrime(ActorRef<WorkerBehavior.Command> worker) {
        getContext().ask(Command.class, worker, Duration.ofSeconds(5),
                me -> new WorkerBehavior.Command("start", me),
                (response, throwable) -> {
                    if (response != null) {
                        return response;
                    }
                    else {
                        System.out.printf("Worker %s failed to respond%n", worker.path());
                        return new NoResponseReceivedCommand(worker);
                    }
                });
    }
}
