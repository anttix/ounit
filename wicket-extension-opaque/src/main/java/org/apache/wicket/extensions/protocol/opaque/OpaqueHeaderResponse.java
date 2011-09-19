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

package org.apache.wicket.extensions.protocol.opaque;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.internal.HeaderResponse;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.response.StringResponse;
import org.apache.wicket.response.filter.IResponseFilter;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

/*
 * In the "normal" world we would just capture all headers with DecoratingHeaderResponse
 * and be done with it. But since OPAQUE implementation in Moodle has it's quirks
 * we have to do some ugly hacks.
 * 
 * The reason for this hackery is that although header contributions are possible,
 * they are not filtered for %%RESOURCES%% thus we can not send links directly to
 * the engine. We have to pump JavaScript directly to the page body, send external
 * css references straight to the header and convert CSS that points to resources
 * into some ugly JavaScript. To add insult to injury, OPAQUE replaces %%RESOURCES%%
 * with a query string that has &-s encoded to &amp;-s so this needs to be decoded first.
 * Go figure!
 * 
 * TODO: Remove this code and replace it with the commented-out block below when
 *       qtype_opaque is fixed.
 * FIXME: Must be able to handle JavaScript resources that contain %%IDPREFIX%%-s 
 */

public class OpaqueHeaderResponse extends HeaderResponse {
	Response real;
	StringResponse buf = new StringResponse();
	Response active = buf;
	OpaqueResponse opaqueResponse;

	OpaqueHeaderResponse(IHeaderResponse response) {
		real = response.getResponse();
		
		RequestCycle requestCycle = RequestCycle.get();
		if(!(requestCycle.getOriginalResponse() instanceof OpaqueResponse))
			throw new IllegalArgumentException(
					"Opaque header extractor can only be used with OpaqueResponse");

		opaqueResponse = (OpaqueResponse)requestCycle.getOriginalResponse();
	}

	@Override
	public void renderCSSReference(ResourceReference reference,
			PageParameters pageParameters, String media, String condition) {
		if (reference == null) {
			throw new IllegalArgumentException("reference cannot be null");
		}
		if (!isClosed()) {
			IRequestHandler handler = new ResourceReferenceRequestHandler(
					reference, pageParameters);
			CharSequence url = RequestCycle.get().urlFor(handler);
			renderCSSReference(url.toString(), media, condition);
		}
	}

	@Override
	public void renderCSSReference(String url, String media, String condition) {
		if (Strings.isEmpty(condition) == false)
		{
			real.write("<!--[if ");
			real.write(condition);
			real.write("]>");
		}
		
		List<String> token = Arrays.asList("css", url.toString(), media);
		real.write("<script type=\"text/javascript\">");
		real.write("var elem=document.createElement(\"link\");");
		real.write("elem.setAttribute(\"rel\",\"stylesheet\");");
		real.write("elem.setAttribute(\"type\",\"text/css\");");
		real.write("elem.setAttribute(\"href\", \"" + url + "\".replace(/&amp;/g, '&'));");
		if (media != null) {
			real.write("elem.setAttribute(\"media\",\"" + media + "\");");
		}
		real.write("document.getElementsByTagName(\"head\")[0].appendChild(elem);");
		real.write("</script>");
		real.write("\n");
		markRendered(token);

		if (Strings.isEmpty(condition) == false)
		{
			real.write("<![endif]-->");
		}
	}

	@Override
	public void renderCSS(CharSequence css, String id) {
		active = real;
		super.renderCSS(css, id);
		active = buf;
	}

	@Override
	public void renderJavaScript(CharSequence javascript, String id) {
		active = real;
		super.renderJavaScript(javascript, id);
		active = buf;
	}

	@Override
	public void renderJavaScriptReference(ResourceReference reference,
			PageParameters pageParameters, String id) {
		active = real;
		super.renderJavaScriptReference(reference, pageParameters, id);
		active = buf;
	}

	@Override
	public void renderJavaScriptReference(java.lang.String url,
			java.lang.String id, boolean defer) {
		active = real;
		super.renderJavaScriptReference(url, id, defer);
		active = buf;
	}

	@Override
	public void renderOnEventJavaScript(String target, String event,
			String javascript) {
		active = real;
		super.renderOnEventJavaScript(target, event, javascript);
		active = buf;
	}

	public void close() {
		super.close();
		AppendingStringBuffer a = new AppendingStringBuffer(buf.getBuffer());
		for (IResponseFilter f : Application.get().getRequestCycleSettings()
				.getResponseFilters()) {
			f.filter(a);
		}
		opaqueResponse.addHeaderContribution(a.toString());
		buf.reset();
	}

	@Override
	protected Response getRealResponse() {
		return active;
	}
}

/*
app.setHeaderResponseDecorator(new IHeaderResponseDecorator() {
	public IHeaderResponse decorate(final IHeaderResponse response) {
		return new DecoratingHeaderResponse(response) {
			StringResponse buf = new StringResponse();
			
			@Override
			public void close() {
				AppendingStringBuffer a = new AppendingStringBuffer(buf.getBuffer());
				for(IResponseFilter f: app.getRequestCycleSettings().getResponseFilters()) {
					f.filter(a);
				}
				OpaqueReturn.get().setHeader(a.toString());
				super.close();
				buf.reset();
			}
			@Override
			public Response getResponse() {
				return buf;
			}
		};
	}
});
*/