public class CurrentStatus implements Runnable {
    private Results results;

    public CurrentStatus(Results results) {
        this.results = results;
    }

    @Override
    public void run() {
        while (this.results.getSize() < 20) {
            System.out.printf("Got %d so far%n", this.results.getSize());
            this.results.print();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
