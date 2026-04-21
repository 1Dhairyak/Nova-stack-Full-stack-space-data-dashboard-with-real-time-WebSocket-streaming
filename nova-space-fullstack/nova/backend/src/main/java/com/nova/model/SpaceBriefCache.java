package com.nova.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * Root cache object stored in Redis under key "nova:brief".
 * Assembled every 5 minutes by SpaceBriefScheduler and injected
 * into every Claude API call as the live-data system prompt context.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceBriefCache implements Serializable {

    private Instant fetchedAt;

    // ISS
    private IssPosition issPosition;

    // Launches
    private List<Launch> upcomingLaunches;

    // Asteroids
    private List<NearEarthObject> nearEarthObjects;

    // Solar / Space weather
    private SpaceWeather spaceWeather;

    // Picture of the day
    private Apod apod;

    // ─── Nested models ────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class IssPosition implements Serializable {
        private double latitude;
        private double longitude;
        private double altitudeKm;
        private double speedKms;
        private Instant timestamp;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Launch implements Serializable {
        private String id;
        private String name;
        private String agency;        // NASA / SpaceX / ISRO / etc.
        private String agencyCode;    // NASA | SPACEX | ISRO
        private String rocket;
        private String launchSite;
        private String launchSiteCountry;
        private Instant windowStart;
        private String status;        // Go / TBD / Hold
        private String missionDesc;
        private String imageUrl;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NearEarthObject implements Serializable {
        private String id;
        private String name;
        private double distanceAu;
        private double distanceKm;
        private double diameterMinKm;
        private double diameterMaxKm;
        private double relativeVelocityKms;
        private boolean potentiallyHazardous;
        private Instant closeApproachDate;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SpaceWeather implements Serializable {
        private double kpIndex;
        private String stormLevel;        // None / G1 / G2 / G3 / G4 / G5
        private List<SolarFlare> flares;
        private Instant fetchedAt;

        @Data @Builder @NoArgsConstructor @AllArgsConstructor
        public static class SolarFlare implements Serializable {
            private String classType;     // A / B / C / M / X + magnitude
            private Instant beginTime;
            private Instant peakTime;
            private Instant endTime;
            private String status;        // ACTIVE / ENDED
            private String region;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Apod implements Serializable {
        private String title;
        private String explanation;
        private String url;
        private String hdUrl;
        private String mediaType;   // image / video
        private String date;
        private String copyright;
    }
}
