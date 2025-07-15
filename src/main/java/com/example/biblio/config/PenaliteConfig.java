package com.example.biblio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "biblio.penalite")
public class PenaliteConfig {

    private int joursGrace = 3;
    private int penaliteBaseJours = 7;
    private int penaliteParJourRetard = 2;
    private boolean verificationAutomatique = true;

    public int getJoursGrace() {
        return joursGrace;
    }

    public void setJoursGrace(int joursGrace) {
        this.joursGrace = joursGrace;
    }

    public int getPenaliteBaseJours() {
        return penaliteBaseJours;
    }

    public void setPenaliteBaseJours(int penaliteBaseJours) {
        this.penaliteBaseJours = penaliteBaseJours;
    }

    public int getPenaliteParJourRetard() {
        return penaliteParJourRetard;
    }

    public void setPenaliteParJourRetard(int penaliteParJourRetard) {
        this.penaliteParJourRetard = penaliteParJourRetard;
    }

    public boolean isVerificationAutomatique() {
        return verificationAutomatique;
    }

    public void setVerificationAutomatique(boolean verificationAutomatique) {
        this.verificationAutomatique = verificationAutomatique;
    }

    @Override
    public String toString() {
        return String.format("Période de grâce: %d jours, Pénalité de base: %d jours, Pénalité par jour de retard: %d jours, Vérification automatique: %s",
            joursGrace, penaliteBaseJours, penaliteParJourRetard, verificationAutomatique ? "Activée" : "Désactivée");
    }
}
