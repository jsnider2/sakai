/**
 * $Id$
 * $URL$
 * EntityUrlCustomizable.java - entity-broker - Apr 11, 2008 6:20:50 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Centre for Applied Research in Educational Technologies, University of Cambridge
 * Licensed under the Educational Community License version 1.0
 * 
 * A copy of the Educational Community License has been included in this 
 * distribution and is available at: http://www.opensource.org/licenses/ecl1.php
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.sakaiproject.entitybroker.entityprovider.capabilities;

import java.util.List;

import org.sakaiproject.entitybroker.entityprovider.EntityProvider;
import org.sakaiproject.entitybroker.util.TemplateParseUtil;
import org.sakaiproject.entitybroker.util.TemplateParseUtil.Template;


/**
 * Indicates that the entity URLs for the types of entities handled by this provider
 * are customized by the set of entity templates returned<br/>
 * These will be used 
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface EntityUrlCustomizable extends EntityProvider {

   /**
    * Defines a set of parseable templates to use with entity url processing for this template 
    * (which is basically a key and the template string),
    * the array which defines the set of template keys is {@link TemplateParseUtil#PARSE_TEMPLATE_KEYS}<br/>
    * Rules for parse templates:<br/>
    * 1) "{","}", and {@link #SEPARATOR} are special characters and must be used as indicated only<br/>
    * 2) Must begin with a {@link #SEPARATOR}, must not end with a {@link #SEPARATOR}<br/>
    * 3) must begin with "/{prefix}" (use the {@link #SEPARATOR} and {@link #PREFIX} constants)<br/>
    * 3) each {var} can only be used once in a template<br/>
    * 4) {var} can never touch each other (i.e /{var1}{var2}/{id} is invalid)<br/>
    * 5) each {var} can only have the chars from {@link TemplateParseUtil#VALID_VAR_CHARS}<br/>
    * 6) parse templates can only have the chars from {@link TemplateParseUtil#VALID_TEMPLATE_CHARS}<br/>
    * 7) Empty braces ({}) cannot appear in the template<br/>
    * <br/>
    * You do not have to supply a template for all the keys in {@link TemplateParseUtil#PARSE_TEMPLATE_KEYS},
    * any that you do not include will simply use the default templates, be careful though,
    * since they will be parsed in order you have to be careful about the order you place your templates
    * in the list, check the default order as an example
    * 
    * @return the list of custom parsing templates in the order they should be processed
    */
   public List<Template> getParseTemplates();

}
