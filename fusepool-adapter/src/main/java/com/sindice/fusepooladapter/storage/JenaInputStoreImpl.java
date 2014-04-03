/**
 * Copyright (c) 2014, Sindice Limited. All Rights Reserved.
 *
 * Fusepool-linker this is proprietary software do not use without authorization by Sindice Limited.
 */

package com.sindice.fusepooladapter.storage;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.clerezza.rdf.core.BNode;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.jena.commons.Tria2JenaUtil;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
/**
 *
 * implementation of {@link com.sindice.fusepooladapter.storage.InputTripleStore } that uses Jena rdf framework
 *
 *
 */
public class JenaInputStoreImpl implements InputTripleStore {
  private static final Logger logger = LoggerFactory.getLogger(JenaInputStoreImpl.class);
  private  final String datafolder;
  private final Tria2JenaUtil t2j;
  
  public JenaInputStoreImpl(String datafolder){
    Path dataPath = Paths.get(datafolder);
    if (Files.exists(dataPath)){
      if (! Files.isDirectory(dataPath)){
        throw new IllegalArgumentException("file instead of folder specified for output data");
      }
    }
    this.datafolder = datafolder;
    this.t2j = new Tria2JenaUtil(new HashMap<BNode, Node>());
  }
  /**
   * cleans and populate the triplestore by triples from input collection 
   */
  public int populate(TripleCollection triples) {
    Path dataPath = Paths.get(datafolder);
    try {
    if (Files.exists(dataPath)){
      DirectoryStream<Path> files;
     
        files = Files.newDirectoryStream(dataPath);
     
      if (files != null){
        for (Path filePath: files){
          Files.delete(filePath);
        }
      } 
    }
    } catch (IOException e) {
      logger.error("error cleanin the store ");
      throw new IllegalArgumentException("wrong datafolder");
    }
    Dataset dataset = TDBFactory.createDataset(datafolder);
    dataset.begin(ReadWrite.WRITE);
    // Get model inside the transaction
    Model model = dataset.getDefaultModel();
    model.removeAll();
    Iterator<Triple> iterator = triples.iterator();
    while (iterator.hasNext()) {
      Triple triple = iterator.next();
      com.hp.hpl.jena.graph.Triple jenaTriple = t2j.convertTriple(triple);
        try {
          model.getGraph().add(jenaTriple);
        } catch (RiotException e1){
          logger.warn("skipping {}", triple.toString());
        }
    }
    int size = (int) model.size();
    dataset.commit();
    TDB.sync(dataset);
    dataset.end();
    return size;
  }

  public void init() {
   //nothing to do in this class

  }

}