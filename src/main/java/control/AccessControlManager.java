package control;

import actors.*;
import db.RedisHelper;

import java.util.*;

public class AccessControlManager {

    public AccessControlManager() {}

    public void addRole(String name, List<Feature> features) {
        Role role = new Role(name);
        if(role.isNameUnique()) {
            role.save();
        }
        assignFeaturesToRole(features, role);
    }

    public void addFeature(String name, List<Role> roles) {
        Feature feature = new Feature(name);
        if(feature.isNameUnique()) {
            feature.save();
        }
        assignFeatureToRoles(feature, roles);
    }

    public void assignFeatureToRole(Feature feature, Role role) {
        feature.grantAccess(role);
        role.addAccess(feature);
    }

    public void assignFeatureToRoles(Feature feature, List<Role> roles) {
        for(Role role : roles) {
            assignFeatureToRole(feature, role);
        }
    }

    public void assignFeaturesToRole(List<Feature> features, Role role) {
        for(Feature feature : features) {
            assignFeatureToRole(feature, role);
        }
    }

    public boolean isAuthorized(Feature feature, Role role) {
        return feature.isAuthorized(role);
    }

    //TODO: below is for POC purposes...
    public static void main(String[] args) {
        String[] roleNames = {"Employee","Manager","Supervisor","District Leader","Regional Leader","Corporate Leader"};
        String[] featureNames = {"A","B","C","D","E","F"};
        HashMap<String,String[]> map = new HashMap<>();
        map.put(roleNames[0], new String[]{"A", "B"});
        map.put(roleNames[1], new String[]{"A","B","C"});
        map.put(roleNames[2], new String[]{"A","B","C","D"});
        map.put(roleNames[3], new String[]{"A","B","C","D","E"});
        map.put(roleNames[4], new String[]{"A","F"});

        RedisHelper.setRedisHost("127.0.0.1");
        AccessControlManager manager = new AccessControlManager();

        for(String feature : featureNames) {
            manager.addFeature(feature, new ArrayList<Role>());
        }

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            String roleName = (String)pair.getKey();
            String[] targetFeatures = (String[])pair.getValue();
            ArrayList<Feature> featureList = new ArrayList<>();
            for(String targetFeature : targetFeatures) {
                featureList.add(new Feature(targetFeature));
            }
            manager.addRole(roleName, featureList);
            it.remove(); // avoids a ConcurrentModificationException
        }

        System.out.println("Is Manager Authorized for B? "+manager.isAuthorized(new Feature("B"),
                new Role("Manager")));
    }
}
