/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.poulpe.web.controller.component;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections.ListUtils;
import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.permissions.ComponentPermission;
import org.jtalks.poulpe.model.dto.PermissionChanges;
import org.jtalks.poulpe.model.dto.PermissionForEntity;
import org.jtalks.poulpe.model.dto.PermissionsMap;
import org.jtalks.poulpe.model.entity.PoulpeGroup;
import org.jtalks.poulpe.service.ComponentService;
import org.jtalks.poulpe.service.GroupService;
import org.jtalks.poulpe.web.controller.SelectedEntity;
import org.jtalks.poulpe.web.controller.TwoSideListWithFilterVm;
import org.jtalks.poulpe.web.controller.WindowManager;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;

/**
 * View Model for editing groups for component permission.
 * 
 * @author Vyacheslav Zhivaev
 */
public class EditGroupsForComponentPermissionVm extends TwoSideListWithFilterVm<PoulpeGroup> {

    public static final String GROUPS_PERMISSIONS_ZUL = "groups/GroupsPermissions.zul";

    // Injected
    private final WindowManager windowManager;
    private final ComponentService componentService;
    private final GroupService groupService;

    // Related to internal state
    private final PermissionForEntity permissionForEntity;
    private final Component component;

    /**
     * Construct VM for editing group list for selected permission.
     * 
     * @param windowManager the window manager instance
     * @param componentService the component service instance
     * @param groupService the group service instance
     * @param selectedEntity the SelectedEntity contains {@link PermissionForEntity} with data needed for construction
     * VM state
     */
    public EditGroupsForComponentPermissionVm(@Nonnull WindowManager windowManager,
            @Nonnull ComponentService componentService, @Nonnull GroupService groupService,
            @Nonnull SelectedEntity<Object> selectedEntity) {
        super();

        permissionForEntity = (PermissionForEntity) selectedEntity.getEntity();
        component = (Component) permissionForEntity.getTarget();

        this.windowManager = windowManager;
        this.componentService = componentService;
        this.groupService = groupService;

        // firstly initialize this state
        stateAfterEdit.addAll(getAlreadyAddedGroups());
    }

    // -- ZK Command bindings --------------------

    /**
     * Closes the dialog.
     */
    @Command
    public void cancel() {
        openGroupsPermissionsWindow();
    }

    /**
     * Saves the state.
     */
    @Command
    public void save() {
        List<PoulpeGroup> alreadyAddedGroups = getAlreadyAddedGroups();

        @SuppressWarnings("unchecked")
        PermissionChanges accessChanges = new PermissionChanges(permissionForEntity.getPermission(),
                ListUtils.subtract(stateAfterEdit, alreadyAddedGroups), ListUtils.subtract(alreadyAddedGroups,
                        stateAfterEdit));

        if (!accessChanges.isEmpty()) {
            if (permissionForEntity.isAllowed()) {
                componentService.changeGrants(component, accessChanges);
            } else {
                componentService.changeRestrictions(component, accessChanges);
            }
        }

        openGroupsPermissionsWindow();
    }

    // -- Utility methods ------------------------

    /**
     * {@inheritDoc}
     */
    @Init
    @Override
    @SuppressWarnings("unchecked")
    public void updateVm() {
        exist.clear();
        exist.addAll(stateAfterEdit);

        avail.clear();
        avail.addAll(ListUtils.subtract(groupService.getAll(), stateAfterEdit));
    }

    /**
     * Gets list of groups which already added in persistence for current {@link Component}.
     * 
     * @return list of groups already added for current {@link Component}
     */
    private List<PoulpeGroup> getAlreadyAddedGroups() {
        ComponentPermission permission = (ComponentPermission) permissionForEntity.getPermission();
        PermissionsMap<ComponentPermission> accessList = componentService.getPermissionsMapFor(component);
        return (permissionForEntity.isAllowed()) ? accessList.getAllowed(permission) : accessList
                .getRestricted(permission);
    }

    /**
     * Opens window with GroupsPermissions page.
     */
    private void openGroupsPermissionsWindow() {
        windowManager.open(GROUPS_PERMISSIONS_ZUL);
    }

}
