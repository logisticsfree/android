package com.example.logisticsfree.models;

import java.util.List;

public class FCMResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int cononical_ids;
    public List<Result> retulst;

    public FCMResponse() {
    }

    public FCMResponse(long multicast_id, int success, int failure, int cononical_ids, List<Result> retulst) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.cononical_ids = cononical_ids;
        this.retulst = retulst;
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCononical_ids() {
        return cononical_ids;
    }

    public void setCononical_ids(int cononical_ids) {
        this.cononical_ids = cononical_ids;
    }

    public List<Result> getRetulst() {
        return retulst;
    }

    public void setRetulst(List<Result> retulst) {
        this.retulst = retulst;
    }
}
