package com.flink.vehiclestate.utils;

/**
 * calculate distance from 2 coordinates
 */

public class GpsDistance {
    private final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    public static double calculateDistance(double userLat, double userLng, double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(userLat))) *
                        (Math.cos(Math.toRadians(venueLat))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AVERAGE_RADIUS_OF_EARTH * c;

    }

    public static void main(String[] args) {
        System.out.println(calculateDistance(39.97, 116.39, 39.97, 116.43));
    }
}
