package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.LogMsg;
import com.sun.istack.internal.Nullable;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterElement<T> {
    @Nullable
    private List<T> inclusion;

    private BooleanProperty changeProperty = new SimpleBooleanProperty(false);

    public boolean isFiltering() {
        return inclusion != null;
    }

    public boolean pass(LogMsg logMsg) {
        return inclusion == null || inclusion.contains(getElement(logMsg));
    }

    public abstract T getElement(LogMsg logMsg);

    public void setFilter(T element) {
        inclusion = new ArrayList<T>();
        inclusion.add(element);
        notifyChange();
    }

    /** If this element filters one and only one element, it will return it. null otherwise. */
    @Nullable
    public T getSingleFilter() {
        if (inclusion != null && inclusion.size() == 1) {
            return inclusion.get(0);
        }

        return null;
    }

    public void clearFilter() {
        inclusion = null;
        notifyChange();
    }

    private void notifyChange() {
        changeProperty.set(!changeProperty.get());
    }

    public Observable getObservable() {
        return changeProperty;
    }

    public void copyFilter(FilterElement<T> source) {
        if (source.inclusion == null) {
            inclusion = null;
        } else {
            inclusion = new ArrayList<T>(source.inclusion);
        }
    }
}
