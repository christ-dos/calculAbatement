package com.myprojet.calculabatement.models;

public enum Month {
    JANVIER,
    FEVRIER,
    MARS,
    AVRIL,
    MAI,
    JUIN,
    JUILLET,
    AOUT,
    SEPTEMBRE,
    OCTOBRE,
    NOVEMBRE,
    DECEMBRE;

    public int getValue() {
        return ordinal() + 1;
    }

    public static Month convertIntToStringOfMonth(int numberOfMonth) {
        Month month;
        switch (numberOfMonth) {
            case 1:
                month = Month.JANVIER;
                break;
            case 2:
                month = Month.FEVRIER;
                break;
            case 3:
                month = Month.MARS;
                break;
            case 4:
                month = Month.AVRIL;
                break;
            case 5:
                month = Month.MAI;
                break;
            case 6:
                month = Month.JUIN;
                break;
            case 7:
                month = Month.JUILLET;
                break;
            case 8:
                month = Month.AOUT;
                break;
            case 9:
                month = Month.SEPTEMBRE;
                break;
            case 10:
                month = Month.OCTOBRE;
                break;
            case 11:
                month = Month.NOVEMBRE;
                break;
            case 12:
                month = Month.DECEMBRE;
                break;
            default:
                return null;
        }
        return month;
    }
}
