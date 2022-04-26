package com.bw.views.chooseview;

import androidx.annotation.AnyRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import java.util.Objects;

/**
 * description: 可选择的item
 *
 * @author houch
 * date: 2022/4/24
 **/
public class ChooseItem {
    private int id;
    private int res;


    public ChooseItem(int id, @DrawableRes int res) {
        this.id = id;
        this.res = res;
    }

    public int getRes() {
        return res;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChooseItem that = (ChooseItem) o;
        return id == that.id && res == that.res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, res);
    }
}
