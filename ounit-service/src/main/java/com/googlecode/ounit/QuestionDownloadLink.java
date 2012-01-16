package com.googlecode.ounit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.protocol.opaque.IVersionedResource;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class QuestionDownloadLink extends ResourceLink<Void> {
	private static final long serialVersionUID = 1L;

	public static class QuestionDownloadResource implements IVersionedResource {
		private static final long serialVersionUID = 1L;

		@Override
		public String getVersionedName() {
			return OunitSession.get().getDownloadFileName();
		}

		@Override
		public void respond(Attributes attributes) {
			File zipFile = OunitSession.get().getDownloadFile();
			
			if(zipFile == null || zipFile.length() <= 0)
				return;

			Response response = attributes.getResponse();
			if(response instanceof WebResponse) {
				WebResponse webResponse = (WebResponse) response;
				webResponse.setAttachmentHeader(getVersionedName());
				webResponse.setContentType("application/octet-stream");
				webResponse.setContentLength(zipFile.length());
			}
			
			try {
				byte [] buf = new byte[4096];
				FileInputStream fin = new FileInputStream(zipFile);
				int count = 0;
				while((count = fin.read(buf)) != -1) {
					if(count == buf.length)
						response.write(buf);
					else {
						response.write(Arrays.copyOf(buf, count));
					}
				}
			} catch(IOException e) {
				throw new WicketRuntimeException(e);
			}
		}
	}

	public QuestionDownloadLink(String id) {
		super(id, new ResourceReference(QuestionDownloadLink.class, "download") {
			private static final long serialVersionUID = 1L;
			final IResource resource = new QuestionDownloadResource();

			@Override
			public IResource getResource() {
				return resource;
			}
		});
	}

	@Override
	protected boolean getStatelessHint() {
		return true;
	}
}
