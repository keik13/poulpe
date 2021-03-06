/**
 * Copyright (C) 2012  JTalks.org Team
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
package org.jtalks.poulpe.service.rest;

import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.jtalks.poulpe.model.dao.UserDao;
import org.jtalks.poulpe.model.entity.PoulpeBranch;
import org.jtalks.poulpe.model.entity.PoulpeSection;
import org.jtalks.poulpe.service.exceptions.JcommuneRespondedWithErrorException;
import org.jtalks.poulpe.service.exceptions.NoConnectionToJcommuneException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.ServerSocket;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.jtalks.poulpe.model.fixtures.TestFixtures.user;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Integration tests that JCommuneNotifierImpl response on commands correctly. It's actually a bit more than unit
 * test because it uses real REST server and real HTTP connections.
 * 
 * @author Evgeny Kapinos
 * @see <a href="https://github.com/mkotsur/restito/blob/master/README.md">Restito</a> 
 */
public class JCommuneNotifierImplIntegrationTest {
    
    private final String JCOMMUNE_URL = "http://localhost";
    
    private final String WHOLEFORUM_URL_PART = "/component";
    private final String REINDEX_URL_PART    = "/search/index/rebuild";
    private final String SECTIONS_URL_PART   = "/sections/";
    private final String BRANCH_URL_PART     = "/branches/";
    
    private final long BRANCH_ID  = 1L;
    private final long SECTION_ID = 1L;
    
    private StubServer server;
    // We don't use interface, because this test 
    // intended for this implementation only
    private JCommuneNotifierImpl notifier;  
   
    /**
     * Start JCommune server mock (Restito framework).
     */
    @BeforeClass
    private void beforeTestCase() {      
        server = new StubServer().run();
        
        //user dao always returns same admin user with same password
        UserDao userDao = mock(UserDao.class);
        doReturn(user("admin", "password", "mail@mail.ru")).when(userDao).getByUsername("admin");
        
        notifier = new JCommuneNotifierImpl(userDao);
    }
        
    /**
     * Shutdown server mock.
     */
    @AfterClass
    public void afterTest() {
        server.stop(); // It works without exceptions even if server mock already stopped
    }
    
   @Test
   public void deleteExistBranch() throws Exception {
       whenHttp(server)
               .match(delete(BRANCH_URL_PART + BRANCH_ID), parameter("password", "password"))
               .then(status(HttpStatus.OK_200));
       notifier.notifyAboutBranchDelete(JCOMMUNE_URL + ":" + String.valueOf(server.getPort()), createBranch());
   }

    @Test(expectedExceptions = JcommuneRespondedWithErrorException.class)
    public void deleteNonexistBranch() throws Exception {
        whenHttp(server)
                .match(delete(BRANCH_URL_PART + BRANCH_ID), parameter("password", "password"))
                .then(status(HttpStatus.NOT_FOUND_404));
        notifier.notifyAboutBranchDelete(JCOMMUNE_URL + ":" + String.valueOf(server.getPort()), createBranch());
    }    

    @Test
    public void deleteExistSection() throws Exception {
        whenHttp(server)
                .match(delete(SECTIONS_URL_PART + SECTION_ID), parameter("password", "password"))
                .then(status(HttpStatus.OK_200));
        notifier.notifyAboutSectionDelete(JCOMMUNE_URL + ":" + String.valueOf(server.getPort()), createSection());
    }    

    @Test(expectedExceptions = JcommuneRespondedWithErrorException.class)
    public void deleteNonexistSection() throws Exception {
        whenHttp(server)
                .match(delete(SECTIONS_URL_PART + SECTION_ID), parameter("password", "password"))
                .then(status(HttpStatus.NOT_FOUND_404));
        notifier.notifyAboutSectionDelete(JCOMMUNE_URL + ":" + String.valueOf(server.getPort()), createSection());
    }
    
    @Test
    public void deleteComponent() throws Exception {
        whenHttp(server)
                .match(delete(WHOLEFORUM_URL_PART), parameter("password", "password"))
                .then(status(HttpStatus.OK_200));
        notifier.notifyAboutComponentDelete(JCOMMUNE_URL+":"+String.valueOf(server.getPort()));    
    }
    
    @Test
    public void reindexComponent() throws Exception {
        whenHttp(server)
                .match(post(REINDEX_URL_PART), parameter("password", "password"))
                .then(status(HttpStatus.OK_200));
        notifier.notifyAboutReindexComponent(JCOMMUNE_URL+":"+String.valueOf(server.getPort()));    
    }    

    @Test(expectedExceptions = NoConnectionToJcommuneException.class)
    public void connectWhenJCommuneOffline() throws Exception {        
        ServerSocket socket = new ServerSocket(0);// Find free port
        socket.close();          
        // Try connect to nonexistent server
        notifier.notifyAboutBranchDelete(JCOMMUNE_URL + ":" + String.valueOf(socket.getLocalPort()), createBranch());
    }

    private PoulpeBranch createBranch() {
        PoulpeBranch branch = new PoulpeBranch();
        branch.setId(BRANCH_ID);
        return branch;
    }

    private PoulpeSection createSection() {
        PoulpeSection section = new PoulpeSection();
        section.setId(SECTION_ID);
        return section;
    }
}
