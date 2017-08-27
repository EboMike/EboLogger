package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostObject;
import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Severity;
import javafx.beans.Observable;
import javafx.beans.property.*;

public class LogFilter {
    private SimpleObjectProperty<Severity> minSeverity = new SimpleObjectProperty<>(Severity.DEBUG);

    private SimpleStringProperty substring = new SimpleStringProperty();

    private BooleanProperty changeProperty = new SimpleBooleanProperty(false);

    private FilterElement<HostThread> threads = new FilterElement<HostThread>() {
        @Override
        public HostThread getElement(LogMsg logMsg) {
            return logMsg.getThread();
        }
    };

    private FilterElement<String> tags = new FilterElement<String>() {
        @Override
        public String getElement(LogMsg logMsg) {
            return logMsg.getTag();
        }
    };

    private FilterElement<HostObject> objects = new FilterElement<HostObject>() {
        @Override
        public HostObject getElement(LogMsg logMsg) {
            return logMsg.getObject();
        }
    };

    public LogFilter() {
        threads.getObservable().addListener(e -> notifyChange());
        objects.getObservable().addListener(e -> notifyChange());
    }

    public LogFilter cloneFilter() {
        LogFilter result = new LogFilter();
        result.minSeverity.set(minSeverity.get());
        result.substring.set(substring.get());
        result.threads.copyFilter(threads);
        result.objects.copyFilter(objects);

        return result;
    }

    public int getMinSeverity() {
        return minSeverity.get().getId();
    }

    public void setMinSeverity(int minSeverity) {
        this.minSeverity.set(Severity.fromId(minSeverity));
    }

    public ObjectProperty<Severity> minSeverityProperty() {
        return minSeverity;
    }

    public String getSubstring() {
        return substring.get();
    }

    public void setSubstring(String substring) {
        this.substring.set(substring);
    }

    public StringProperty substringProperty() {
        return substring;
    }

    public boolean passesFilter(LogMsg msg) {
        if (msg.getSeverity() < getMinSeverity()) {
            return false;
        }

        if (!objects.pass(msg)) {
            return false;
        }

        if (!threads.pass(msg)) {
            return false;
        }

        if (!tags.pass(msg)) {
            return false;
        }

        if (substring.get() != null && substring.get().length() > 0) {
            if (!msg.getMsg().contains(substring.get()) &&
                    (msg.getTag() == null || !msg.getTag().contains(substring.get()))) {
                return false;
            }
        }

        return true;
    }

    public void setObjectFilter(HostObject object) {
        objects.setFilter(object);
    }

    public void clearObjectFilter() {
        objects.clearFilter();
    }

    public void setThreadFilter(HostThread thread) {
        threads.setFilter(thread);
    }

    public void clearThreadFilter() {
        threads.clearFilter();
    }

    public void setTagFilter(String tag) {
        tags.setFilter(tag);
    }

    public void clearTagFilter() {
        tags.clearFilter();
    }

    private void notifyChange() {
        changeProperty.set(!changeProperty.get());
    }

    public Observable getObservable() {
        return changeProperty;
    }

    /**
     * If true, this filter will not filter anything.
     */
    public boolean isPassthrough() {
        return getMinSeverity() == 0 && (substring.get() == null || substring.get().length() == 0)
                && !objects.isFiltering() && !threads.isFiltering();
    }

    public boolean hasObjectFilter() {
        return objects.isFiltering();
    }

    public boolean hasThreadFilter() {
        return threads.isFiltering();
    }

    public boolean hasTagFilter() {
        return tags.isFiltering();
    }

    public String createLabel() {
        StringBuilder result = new StringBuilder(128);

        if (threads.isFiltering()) {
            return threads.getSingleFilter().getName();
        }

        return "TODO";
    }
}
