package org.jtalks.poulpe.web.controller.zkmacro;

import org.jtalks.poulpe.model.entity.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stanislav bashkirtsev
 */
public class BranchPermissionRow {
    private List<Group> groups;
    private String rowLabel;

    protected BranchPermissionRow(String rowLabel, List<Group> groups) {
        this.groups = groups;
        this.rowLabel = rowLabel;
    }

    public List<Group> getGroups() {
        return groups;
    }


    public String getRowLabel() {
        return rowLabel;
    }

    public BranchPermissionRow addGroup(Group group) {
        groups.add(group);
        return this;
    }

    public static BranchPermissionRow newAllowRow() {
        return newAllowRow(new ArrayList<Group>());
    }

    public static BranchPermissionRow newRestrictRow() {
        return newRestrictRow(new ArrayList<Group>());
    }

    public static BranchPermissionRow newAllowRow(List<Group> groups) {
        return new BranchPermissionRow("Allow", groups);
    }

    public static BranchPermissionRow newRestrictRow(List<Group> groups) {
        return new BranchPermissionRow("Restrict", groups);
    }

}