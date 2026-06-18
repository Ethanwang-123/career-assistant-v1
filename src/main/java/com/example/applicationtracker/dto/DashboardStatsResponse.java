package com.example.applicationtracker.dto;

public class DashboardStatsResponse {

    private long totalApplications;
    private long totalInterviews;
    private long totalOffers;
    private long totalRejected;

    public DashboardStatsResponse(long totalApplications, long totalInterviews, long totalOffers, long totalRejected) {
        this.totalApplications = totalApplications;
        this.totalInterviews = totalInterviews;
        this.totalOffers = totalOffers;
        this.totalRejected = totalRejected;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public long getTotalInterviews() {
        return totalInterviews;
    }

    public void setTotalInterviews(long totalInterviews) {
        this.totalInterviews = totalInterviews;
    }

    public long getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(long totalOffers) {
        this.totalOffers = totalOffers;
    }

    public long getTotalRejected() {
        return totalRejected;
    }

    public void setTotalRejected(long totalRejected) {
        this.totalRejected = totalRejected;
    }
}
