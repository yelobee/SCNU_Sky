package com.example.administrator.huashixingkong.model;

import java.util.List;

/**
 * Created by yelobee_ on 16/4/25.
 */
public class ActivityData {
    private List<ActiveActivity> activitys;

    public List<ActiveActivity> getActivitys() {
        return activitys;
    }

    public void setActivitys(List<ActiveActivity> activitys) {
        this.activitys = activitys;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "have " + activitys.size() + " activitiess";
    }
}
