package control;

import actors.*;

import java.util.List;

public class AccessControlManager {

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

    public static void main(String[] args) {
        
    }
}
