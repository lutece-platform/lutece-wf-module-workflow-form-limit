/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.limit.service;

import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfiguration;
import fr.paris.lutece.plugins.form.modules.exportdirectory.business.FormConfigurationHome;
import fr.paris.lutece.plugins.form.modules.exportdirectory.service.ExportdirectoryPlugin;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.workflow.modules.limit.business.TaskLimitConfig;
import fr.paris.lutece.plugins.workflow.modules.limit.business.TaskLimitConfigDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskLimit
 *
 * La library-workflow-core offre une autre classe abstraite SimpleTask
 * implémentant les divers méthodes. Cette classe permet aux implémentations
 * dans les modules de se passer de l'implémentation de certaines méthodes
 * inutiles dans leur cas de figure.
 */
public class TaskLimit extends SimpleTask
{
    private static final String FIELD_NUMBER_LIMIT = "module.workflow.limit.task_limit_config.label_task_number";
    private static final String BEAN_TASK_CONFIG_SERVICE = "workflow-limit.taskLimitConfigService";

    // SERVICES
    @Inject
    @Named( BEAN_TASK_CONFIG_SERVICE )
    private ITaskConfigService _taskLimitConfigService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        Plugin pluginForm = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Plugin pluginExportDirectory = PluginService.getPlugin( ExportdirectoryPlugin.PLUGIN_NAME );
        TaskLimitConfig config = _taskLimitConfigService.findByPrimaryKey( this.getId(  ) );
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        if ( ( config != null ) && ( resourceHistory != null ) )
        {
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
            RecordFieldFilter recordFilter = new RecordFieldFilter(  );

            int nidForm = -1;

            Collection<FormConfiguration> listExportDirectory = FormConfigurationHome.findAll( pluginExportDirectory );

            for ( FormConfiguration formConfiguration : listExportDirectory )
            {
                if ( formConfiguration.getIdDirectory(  ) == record.getDirectory(  ).getIdDirectory(  ) )
                {
                    nidForm = formConfiguration.getIdForm(  );
                }
            }

            //decrease the config task
            Form form = FormHome.findByPrimaryKey( nidForm, pluginForm );

            if ( form != null )
            {
                int nNumber = config.getNumber(  );

                //validator
                recordFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

                List<Record> allrecords = RecordHome.getListRecord( recordFilter, pluginDirectory );

                 nNumber--;
         
                if ( nNumber <= 0  )
                {
                    
                    form.setActive( false );
                    form.setAutoPublicationActive( false );
                    FormHome.update( form, pluginForm );
                }
              
                   
              

                config.setNumber( nNumber );

                TaskLimitConfigDAO taskConfigDOA = new TaskLimitConfigDAO(  );
                taskConfigDOA.store( config );

                //                
                //                if (nNumber > 0) {
                //                    nNumber--;
                //                    config.setNumber(nNumber);
                //                    TaskLimitConfigDAO taskConfigDOA = new TaskLimitConfigDAO();
                //                    taskConfigDOA.store(config);
                //                } else {
                //                    form.setActive(false);
                //                    form.setAutoPublicationActive(false);
                //                    FormHome.update(form, pluginForm);
                //                }
                //  I18nService.getLocalizedString( MESSAGE_LIMIT_MAX_VALUE, locale )
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        _taskLimitConfigService.remove( this.getId(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskLimitConfig config = _taskLimitConfigService.findByPrimaryKey( this.getId(  ) );

        if ( config != null )
        {
            return I18nService.getLocalizedString( FIELD_NUMBER_LIMIT, locale ) + " = " + config.getNumber(  );
        }

        return StringUtils.EMPTY;
    }
}
