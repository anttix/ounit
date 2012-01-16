package org.apache.wicket.extensions.protocol.opaque;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class MockSecondPage extends MockBasePage {
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;

	public MockSecondPage(PageParameters parameters) {
		super(parameters);
		log.debug("MockSecondPage()");
		
		final MockModelObject m = (MockModelObject)getDefaultModelObject();
		setStatelessHint(false);
		
		mainForm.add(new TextField<Integer>("nr"));
		mainForm.add(new Button("go") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				log.debug("Go clicked");
				if(m.getNr() == -1)
				    setResponsePage(MockHomePage.class);				
			}
		});
	}
}
