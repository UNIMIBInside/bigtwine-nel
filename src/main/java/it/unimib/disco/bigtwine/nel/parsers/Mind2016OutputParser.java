package it.unimib.disco.bigtwine.nel.parsers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.commons.csv.CSVReader;
import it.unimib.disco.bigtwine.commons.csv.CSVRecord;
import it.unimib.disco.bigtwine.commons.models.LinkedTweet;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Mind2016OutputParser implements OutputParser {

    private Reader reader;
    private CSVFactory csvFactory;
    private CSVReader csvReader;
    private static final char delimiter = '\t';
    private CSVRecord nextRecord;
    private LinkedTweet nextTweet;

    public Mind2016OutputParser(CSVFactory csvFactory) {
        this.csvFactory = csvFactory;
    }


    @Override
    public Reader getReader() {
        return reader;
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public CSVReader getCsvReader() throws IOException {
        if (this.csvReader == null) {
            this.csvReader = this.csvFactory.getReader(reader, delimiter);
        }

        return this.csvReader;
    }

    private boolean isValidTweet(LinkedTweet tweet) {
        return true;
    }

    private LinkedTweet.Entity parseEntity(CSVRecord record) {
        if (record.size() == 0) return null;

        try {
            int posStart = Integer.parseInt(record.get(1));
            int posEnd = Integer.parseInt(record.get(2));
            String linkOrNilCluster = record.get(3).trim();
            float confidence = Float.parseFloat(record.get(4));
            String category = record.get(5).trim();
            boolean isNil = linkOrNilCluster.toUpperCase().startsWith("NIL");

            return new LinkedTweet.Entity(
                new LinkedTweet.EntityTextRange(posStart, posEnd),
                linkOrNilCluster,
                confidence,
                category,
                isNil);

        }catch(IllegalArgumentException e) {
            return null;
        }
    }

    private LinkedTweet parse() throws IOException {
        Iterator<CSVRecord> csv = this.getCsvReader().iterator();

        if (this.nextTweet == null) {
            this.nextTweet = new LinkedTweet();
        }

        CSVRecord next;
        if (this.nextRecord != null) {
            next = this.nextRecord;
            this.nextRecord = null;
        }else if (csv.hasNext()) {
            next = csv.next();
        }else {
            return null;
        }

        List<LinkedTweet.Entity> entities = new ArrayList<>();

        while(next != null) {
            CSVRecord current = next;
            next = null;

            if (current.size() != 6) {
                if (csv.hasNext()) {
                    next = csv.next();
                }
                continue;
            }

            String tweetId = current.get(0).trim();
            if (this.nextTweet.getId() == null) {
                this.nextTweet.setId(tweetId);
            }

            if (!tweetId.equals(this.nextTweet.getId())) {
                this.nextRecord = current;
                break;
            }else if (csv.hasNext()) {
                next = csv.next();
            }

            LinkedTweet.Entity entity = this.parseEntity(current);

            if (entity != null) {
                entities.add(entity);
            }
        }

        this.nextTweet.setEntities(entities.toArray(new LinkedTweet.Entity[0]));

        if (this.isValidTweet(this.nextTweet)) {
            return this.nextTweet;
        }else {
            return this.parse();
        }
    }

    @Override
    public LinkedTweet[] items() {
        List<LinkedTweet> tweets = new ArrayList<>();
        while (this.hasNext()) {
            tweets.add(this.nextTweet);
            this.nextTweet = null;
        }

        return tweets.toArray(new LinkedTweet[0]);
    }

    @Override
    public boolean hasNext() {
        if (this.nextTweet == null) {
            try {
                this.nextTweet = this.parse();
            } catch (IOException e) {
                this.nextTweet = null;
            }
        }

        return this.nextTweet != null;
    }

    @Override
    public LinkedTweet next() {
        if (this.hasNext()) {
            LinkedTweet tweet = this.nextTweet;
            this.nextTweet = null;
            return tweet;
        }

        throw new NoSuchElementException();
    }
}
