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

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Entity;
import org.jtalks.poulpe.model.entity.Jcommune;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.model.entity.TopicType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.zkoss.zul.TreeNode;

import java.util.Arrays;
import java.util.List;

import static org.jtalks.poulpe.web.controller.utils.ObjectsFactory.*;
import static org.testng.Assert.*;

/**
 * The test class for {@link TreeNodeFactory}
 *
 * @author Konstantin Akimov
 */
public class TreeNodeFactoryTest {
    PoulpeSection emptySection = fakeSection();
    PoulpeSection section = sectionWithBranches();
    List<PoulpeSection> sections = Arrays.asList(section, section);

    PoulpeBranch branch = fakeBranch();

    @Test(dataProvider = "provideJcommuneWithSectionsAndBranches")
    public void testBuildForumStructure(Jcommune jcommune) throws Exception {
        ExtendedTreeNode root = TreeNodeFactory.buildForumStructure(jcommune);
        assertEquals(root.getChildCount(), 2);
        assertSame(root.getChildAt(0).getChildAt(0).getData(), jcommune.getSections().get(0).getBranches().get(0));
        assertSame(root.getChildAt(1).getChildAt(1).getData(), jcommune.getSections().get(1).getBranches().get(1));
    }

    @Test
    public void getTreeNodeEmptySection() {
        ExtendedTreeNode<PoulpeSection> testNode = TreeNodeFactory.getTreeNode(emptySection);

        assertEquals(testNode.getData(), emptySection);
        assertTrue(testNode.getChildren().isEmpty());
        assertTrue(testNode.isExpanded());
    }

    @Test
    public void getTreeNodeBranch() {
        ExtendedTreeNode<PoulpeBranch> testNode = TreeNodeFactory.getTreeNode(branch);

        assertEquals(testNode.getData(), branch);
        assertNull(testNode.getChildren());
        assertTrue(testNode.isExpanded());
        assertTrue(testNode.isLeaf());
    }

    @Test
    public void getTreeNodeNull() {
        ExtendedTreeNode<?> testNode = TreeNodeFactory.getTreeNode((Entity) null);
        assertNull(testNode);
    }

    @Test
    public void getTreeNodeNotSuitableEntity() {
        ExtendedTreeNode<?> testNode = TreeNodeFactory.getTreeNode(new TopicType());
        assertNull(testNode);
    }

    /**
     * Test suitable entities with relations
     */
    @Test
    public void getTreeNodeWithRelationsTest() {
        ExtendedTreeNode<?> testNode = TreeNodeFactory.getTreeNode(section);

        assertEquals(testNode.getData(), section);
        assertEquals(testNode.getChildren().size(), section.getPoulpeBranches().size());
        assertTrue(testNode.isExpanded());

        assertNotNull(testNode.getChildAt(0));

        assertTrue(testNode.getChildAt(0).getData() instanceof PoulpeBranch);
        assertEquals(testNode.getChildAt(0).getData(), section.getPoulpeBranches().get(0));
    }

    @Test
    public void getTreeNodesTest() {
        List<ExtendedTreeNode<PoulpeSection>> nodes = TreeNodeFactory.getTreeNodes(sections);

        assertEquals(nodes.size(), sections.size());
        assertSectionsContainsAllBranches(nodes);
    }

    private void assertSectionsContainsAllBranches(List<ExtendedTreeNode<PoulpeSection>> nodes) {
        for (ExtendedTreeNode<PoulpeSection> node : nodes) {
            List<Branch> childBranches = node.getData().getBranches();
            assertEquals(node.getChildCount(), childBranches.size());
            assertChildrenAreLeafs(node);
        }
    }

    @SuppressWarnings("unchecked")
    private void assertChildrenAreLeafs(ExtendedTreeNode<PoulpeSection> node) {
        for (Object obj : node.getChildren()) {
            ExtendedTreeNode<PoulpeBranch> subnode = (ExtendedTreeNode<PoulpeBranch>) obj;
            assertTrue(subnode.isLeaf());
        }
    }

    @Test
    public void getTreeNodesWithNullsTest() {
        List<PoulpeSection> sections = Arrays.asList(section, null, section, null);
        List<ExtendedTreeNode<PoulpeSection>> nodes = TreeNodeFactory.getTreeNodes(sections);

        assertEquals(nodes.size(), 2);
        assertSectionsContainsAllBranches(nodes);
    }

    @DataProvider
    private Object[][] provideJcommuneWithSectionsAndBranches() {
        Jcommune jcommune = new Jcommune();
        PoulpeSection sectionA = new PoulpeSection("SectionA");
        sectionA.addOrUpdateBranch(new PoulpeBranch("BranchA"));
        sectionA.addOrUpdateBranch(new PoulpeBranch("BranchB"));
        jcommune.addSection(sectionA);
        PoulpeSection sectionB = new PoulpeSection("SectionB");
        sectionB.addOrUpdateBranch(new PoulpeBranch("BranchD"));
        sectionB.addOrUpdateBranch(new PoulpeBranch("BranchE"));
        jcommune.addSection(sectionB);
        return new Object[][]{{jcommune}};
    }

}
