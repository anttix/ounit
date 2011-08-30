/*
 * OUnit - an OPAQUE compliant framework for Computer Aided Testing
 *
 * Copyright (C) 2010, 2011  Antti Andreimann
 *
 * This file is part of OUnit.
 *
 * OUnit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OUnit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OUnit.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.googlecode.ounit;

import java.io.File;
import java.io.FileWriter;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.protocol.opaque.OpaqueReturn;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.googlecode.ounit.executor.OunitResult;
import com.googlecode.ounit.executor.OunitTask;

/**
 * 
 * OUnit main view.
 * 
 * @author anttix
 *
 */
public class MainPage extends BasePage {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	public MainPage(PageParameters parameters) {
		super(parameters);
		log.debug("MainPage()");
		
		WebMarkupContainer quizPanel = new WebMarkupContainer("questiondiv");
		mainForm.add(quizPanel);
		quizPanel.add(new QuizStateAttributeModifier(getOunitModel(),
				"class", "ou-question", "ou-closed-question"));

		final Component description = new HtmlFile("description");
		quizPanel.add(description);
		quizPanel.add(new AnchorLink("descriptionlink", description));
		
		final Component results = new HtmlFile("resultsFile");
		quizPanel.add(results);
		quizPanel.add(new WebMarkupContainer("resultscaption") {
			private static final long serialVersionUID = 1L;
			protected void onConfigure() {
				super.onConfigure();
				results.configure();
				setVisible(results.isVisible());
			};
		}.add(new AnchorLink("resultslink", results)));

		/*
		 * Generate TextAreas first, because we need editor objects as anchors
		 * for the links
		 */	
		ListView<ProjectTreeNode> lv = new ListView<ProjectTreeNode>("editors") {
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<ProjectTreeNode> item) {
				ProjectTreeNode node = item.getModelObject();
				node.setEditor(item);
				TextArea<ProjectTreeNode> ta = new TextArea<ProjectTreeNode>("editorarea",
						new PropertyModel<ProjectTreeNode>(node, "fileContents"));
				ta.add(new SimpleAttributeModifier("title", node.getName()));
				ta.add(new QuizStateAttributeModifier(getOunitModel(),
						"readonly", null, "readonly"));
				item.add(ta);
				item.setOutputMarkupId(true);
		    }
		};
		quizPanel.add(lv);
		lv.setReuseItems(true);
		/* Force ListView to populate itself RIGHT NOW so state-less forms can work */
		// FIXME: This is an internal function. Maybe implement some hack like this
		//        http://osdir.com/ml/users-wicket.apache.org/2009-02/msg00925.html
		lv.internalPrepareForRender(false);
		
		/*
		 * Populate tab header links
		 */
		quizPanel.add(new ListView<ProjectTreeNode>("editorcaptions") {
			private static final long serialVersionUID = 1L;
			protected void populateItem(ListItem<ProjectTreeNode> item) {
				ProjectTreeNode node = item.getModelObject();
				item.add(new AnchorLink("editorlink", node.getEditor(), node.getName()));
			}
		}.setReuseItems(true));

		final Component tree = new ExplorerTreePanel("tree");
		quizPanel.add(tree);		
		quizPanel.add(new WebMarkupContainer("treecaption") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfigure() {
				super.onConfigure();
				tree.configure();
				setVisible(tree.isVisible());
			}
		});
		
		mainForm.add(new Button("submit") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(!getOunitSession().isClosed());
			}

			@Override
			public void onSubmit() {
				OunitTask task = OunitService.requestBuild(
						OpaqueReturn.get().getEngineSessionId());
				OunitResult r = OunitService.waitForTask(task);
				
				OunitSession sess = getOunitSession();
				if(r.hasErrors()) {
					// TODO: This logic should be somewhere else
					File rf = getOunitSession().getResultsFile();
					log.debug("Build failed with errors: {}", r.getErrors());
					rf.getParentFile().mkdirs();
					try {
						FileWriter wr = new FileWriter(rf);
						wr.append("<pre>");
						wr.append(r.getErrors());
						wr.append("</pre>");
						wr.close();
					} catch(Exception e) {
						log.warn("Failed to save result", e);
						throw new RuntimeException(e);
					}
				} else {
					// Successful build, see if we can get a (partial) grade
					int marks = sess.getMarks();
					
					if(marks == sess.getMaxMarks()) {
						// Max marks, grade NOW!
						sess.setClosed(true);
					} else {
						// Partial marks
						setResponsePage(ConfirmPage.class);
					}
				}
			}
		});
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.renderJavaScriptReference(new PackageResourceReference(
				MainPage.class, "codemirror/codemirror-compressed.js"));
		response.renderCSSReference(new PackageResourceReference(
				MainPage.class, "codemirror/codemirror.css"));
		response.renderCSSReference(new PackageResourceReference(
				MainPage.class, "codemirror/codemirror-allmodes.css"));
		response.renderJavaScriptReference(new PackageResourceReference(
				MainPage.class, "MainPage.js"));
	}
}