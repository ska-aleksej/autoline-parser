package ru.parser.model;

public class CompanyDetails {

    private String address;
    private String adsCount;
    private String yearsOnSite;
    private String yearsOnMarket;

    public CompanyDetails(String adsCount, String yearsOnSite, String yearsOnMarket, String address) {
        this.adsCount = adsCount;
        this.yearsOnSite = yearsOnSite;
        this.yearsOnMarket = yearsOnMarket;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAdsCount() {
        return adsCount;
    }

    public void setAdsCount(String adsCount) {
        this.adsCount = adsCount;
    }

    public String getYearsOnSite() {
        return yearsOnSite;
    }

    public void setYearsOnSite(String yearsOnSite) {
        this.yearsOnSite = yearsOnSite;
    }

    public String getYearsOnMarket() {
        return yearsOnMarket;
    }

    public void setYearsOnMarket(String yearsOnMarket) {
        this.yearsOnMarket = yearsOnMarket;
    }
}
