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

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;

public class ExplorerTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private boolean root = true;
	ProjectTree tree;
	
	public ExplorerTreePanel(String id) {
		this(id, (ProjectTree) null);
	}
	
	public ExplorerTreePanel(String id, ProjectTree tree) {
		super(id);

		if(tree != null)
			this.tree = tree;
	}
	
	private ExplorerTreePanel(String id, List<ProjectTreeNode> children) {
		super(id);
		root = false;
		add(new Rows("rows", children));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		if(!root) return;
		
		if(tree == null)
			tree = (ProjectTree)getDefaultModelObject();
		
		add(new Rows("rows", tree.getTrunk()));
		//setVersioned(false);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		
		if(!root) return;

		/*
		 * If there is no Read Only nodes to display and the number of read-write nodes is
		 * reasonable, there is no need to waste space on the project treeview.
		 */

		// FIXME: Shouldn't access session directly.
		//        better use a model property.
		//OunitSession sess = (OunitSession)getPage().getDefaultModelObject();
		//OunitSession sess = ((BasePage)getPage()).getOunitSession();
		OunitSession sess = OunitSession.get();
		
		setVisible(tree.getRoNodes().size() > 0
				|| tree.getRwNodes().size() >= 6
				|| sess.hasDownload());
	}

	private class Rows extends ListView<ProjectTreeNode> {
		private static final long serialVersionUID = 1L;
		
		public Rows(String id, List<? extends ProjectTreeNode> list) {
			super(id, list);
		}
		
		public class TreeFragment extends Fragment {
			private static final long serialVersionUID = 1L;

			public TreeFragment(String id, String markupId) {
				super(id, markupId, ExplorerTreePanel.this);
			}
		}
		
		@Override
		protected void populateItem(ListItem<ProjectTreeNode> item) {
		    ProjectTreeNode node = (ProjectTreeNode)item.getDefaultModelObject();
            WebMarkupContainer row = new WebMarkupContainer("row");
            Fragment frag;
            
            if (node.getChildren() != null) {
            	frag = new TreeFragment("listitem", "branch");
            	frag.add(new Label("label", node.getName()));
            	frag.add(new ExplorerTreePanel("nested", node.getChildren()));
            } else {
            	frag = new TreeFragment("listitem", "leaf");
                if(node.isReadonly()) {
                	// FIXME: Make it work!
                	frag.add(new Label("link", node.getName()));
                	
                	//frag.add(new ExternalLink("link", node.getName(), node.getName())
                	//			.add(new SimpleAttributeModifier("target", "_blank")));
                	//AbstractLink link = new ResourceLink<String>("link",
    				//			new ResourceStreamResource(new FileResourceStream(
    				//					node.getPathname())));
                	//link.setBody(Model.of(node.getName()));
                    //row.add(link);
                } else {
                	frag.add(new AnchorLink("link", node.getEditor(), node.getName()));
                }
            }
        	row.add(frag);
            item.add(row);
		}
	}
}
