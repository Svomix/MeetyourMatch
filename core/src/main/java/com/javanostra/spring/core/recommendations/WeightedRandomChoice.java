package com.javanostra.spring.core.recommendations;


import com.javanostra.spring.core.entities.UserRecInterests;

import java.util.List;
import java.util.Random;

public class WeightedRandomChoice {
    public static UserRecInterests weightedChoice(List<UserRecInterests> userRecInterestsList) {
        double totalWeight = 0.0;
        for (UserRecInterests userRecInterests : userRecInterestsList) {
            totalWeight += userRecInterests.getWeight();
        }

        Random random = new Random();
        double r = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;
        for (int i = 0; i < userRecInterestsList.size(); i++) {
            cumulativeWeight += userRecInterestsList.get(i).getWeight();
            if (r <= cumulativeWeight) {
                return userRecInterestsList.get(i);
            }
        }
        return userRecInterestsList.get(random.nextInt(userRecInterestsList.size()));
    }
}
