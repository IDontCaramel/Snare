package net.caramel.snare.setting;
public final class NumberSetting extends Setting<Double> {
    private final double min, max, step;
    public NumberSetting(String id, String name, String description, double value, double min, double max, double step) {
        super(id, name, description, validate(value, min, max, step), () -> true);
        this.min = min; this.max = max; this.step = step;
    }
    private static double validate(double value, double min, double max, double step) {
        if (!Double.isFinite(min) || !Double.isFinite(max) || min > max || !Double.isFinite(step) || step <= 0) throw new IllegalArgumentException("invalid number range");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("default number must be finite");
        double rounded = min + Math.round((Math.max(min, Math.min(max, value)) - min) / step) * step;
        return Math.max(min, Math.min(max, rounded));
    }
    @Override protected Double normalize(Double candidate) {
        if (!Double.isFinite(candidate)) return defaultValue();
        double rounded = min + Math.round((Math.max(min, Math.min(max, candidate)) - min) / step) * step;
        return Math.max(min, Math.min(max, rounded));
    }
    public double min() { return min; } public double max() { return max; } public double step() { return step; }
    public double ratio() { return max == min ? 0 : (value() - min) / (max - min); }
    public void setRatio(double ratio) { setValue(min + Math.max(0, Math.min(1, ratio)) * (max - min)); }
}