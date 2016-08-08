package com.unicorn.common.config;

import java.util.concurrent.TimeUnit;

/**
 * TimeItem is a class that is used to prevent arbitrary long values from existing in our project.
 * Instead, this class can be used to help our code by improving readability.
 */
public class TimeItem {

    private long value = 0;
    private String unit = "milliseconds";

    public TimeItem() {
    }

    public TimeItem(long value, String unit) {
        setValue(value);
        setUnit(unit);
    }

    public TimeItem withValue(long value) {
        setValue(value);
        return this;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public TimeItem withUnit(String unit) {
        setUnit(unit);
        return this;
    }

    public boolean hasUnit() {
        return this.unit != null && !this.unit.isEmpty();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private TimeUnit getUnitAsTimeUnit(String unit) {
        TimeUnit currentTimeUnit = null;
        if (unit != null) {
            switch (unit.toLowerCase()) {
                case "days":
                    currentTimeUnit = TimeUnit.DAYS;
                    break;
                case "hours":
                    currentTimeUnit = TimeUnit.HOURS;
                    break;
                case "minutes":
                    currentTimeUnit = TimeUnit.MINUTES;
                    break;
                case "seconds":
                    currentTimeUnit = TimeUnit.SECONDS;
                    break;
                case "microseconds":
                    currentTimeUnit = TimeUnit.MICROSECONDS;
                    break;
                case "milliseconds":
                    currentTimeUnit = TimeUnit.MILLISECONDS;
                    break;
                case "nanoseconds":
                    currentTimeUnit = TimeUnit.NANOSECONDS;
                    break;
            }
        }
        return currentTimeUnit;
    }

    public long getValueAs(TimeUnit timeUnit) {
        long returnValue = 0;
        if (this.hasUnit()) {
            TimeUnit currentTimeUnit = getUnitAsTimeUnit(this.getUnit());
            switch (timeUnit) {
                case DAYS:
                    returnValue = currentTimeUnit.toDays(this.getValue());
                    break;
                case HOURS:
                    returnValue = currentTimeUnit.toHours(this.getValue());
                    break;
                case MINUTES:
                    returnValue = currentTimeUnit.toMinutes(this.getValue());
                    break;
                case SECONDS:
                    returnValue = currentTimeUnit.toSeconds(this.getValue());
                    break;
                case MICROSECONDS:
                    returnValue = currentTimeUnit.toMicros(this.getValue());
                    break;
                case MILLISECONDS:
                    returnValue = currentTimeUnit.toMillis(this.getValue());
                    break;
                case NANOSECONDS:
                    returnValue = currentTimeUnit.toNanos(this.getValue());
                    break;
            }
        }
        return returnValue;
    }


}
