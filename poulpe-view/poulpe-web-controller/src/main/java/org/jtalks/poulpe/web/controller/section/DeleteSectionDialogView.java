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
package org.jtalks.poulpe.web.controller.section;

import java.util.List;

import org.jtalks.poulpe.model.entity.PoulpeSection;

/**
 * This interface for representation view delete section dialog
 * 
 * @author Bekrenev Dmitry
 * @author Alexey Grigorev
 */
public interface DeleteSectionDialogView {

    /**
     * @return section which will be deleted
     */
    PoulpeSection getSectionToDelete();

    /**
     * Get selected section in combobox
     * 
     * @return section which will receive branches of the section being deleted,
     * or null if none selected
     */
    PoulpeSection getRecipientSection();

    /**
     * @return deleting mode
     */
    SectionDeleteMode getDeleteMode();

    /**
     * Causes the dialog to show
     */
    void showDialog();
    
    /**
     * Causes the dialog to show
     */
    void showDialog(PoulpeSection section);

    /**
     * Causes the dialog to close
     */
    void closeDialog();

    /**
     * Initializes combobox with available section
     * 
     * @param sections list available sections
     */
    void initSectionsCombobox(List<PoulpeSection> sections);

    /**
     * Cleans the combobox with sections and disables it, used when no 
     * sections are available
     */
    void initEmptyAndDisabledCombobox();
}
