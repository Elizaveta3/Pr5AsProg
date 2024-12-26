class Route {
    private String transportType;
    private int cost;
    private int time;

    public Route(String transportType, int cost, int time) {
        this.transportType = transportType;
        this.cost = cost;
        this.time = time;
    }

    public String getTransportType() {
        return transportType;
    }

    public int totalCost() {
        return cost;
    }

    public int totalTime() {
        return time;
    }

    @Override
    public String toString() {
        return transportType + " (ціна: " + cost + ", час: " + time + " годин)";
    }
}
