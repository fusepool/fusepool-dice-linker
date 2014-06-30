package com.sindice.fusepooladapter;

import org.apache.clerezza.rdf.core.TripleCollection;
import org.osgi.service.component.annotations.Component;

import com.sindice.fusepooladapter.configuration.DbpediaLinkerConfiguration;

import eu.fusepool.datalifecycle.Interlinker;

/**
 * A concrete implementation of {@link LinkerAdapter} for deduplication of DbPedia companies
 * 
 */
@Component(service = Interlinker.class)
public class DbpediaLinkerAdapter extends LinkerAdapter {

    @Override
	public TripleCollection interlink(TripleCollection dataToInterlink) {
		return interlink(dataToInterlink, DbpediaLinkerConfiguration.getInstance());
	}

	@Override
	public TripleCollection interlink(TripleCollection dataset1, TripleCollection dataset2) {
		throw new UnsupportedOperationException("Not supported. Use PatentsDbpediaLinkerAdapter");
	}

	@Override
	public String getName() {
		return "duke-interlinker "+ this.getClass().getName();
	}

}
