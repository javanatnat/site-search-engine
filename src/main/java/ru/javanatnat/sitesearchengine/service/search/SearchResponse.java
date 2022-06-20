package ru.javanatnat.sitesearchengine.service.search;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    private static final SearchResponse EMPTY_RESPONSE = new SearchResponse(0);

    private boolean result;
    private long count;
    private List<SearchData> dataList;

    public SearchResponse() {
        this(0, new ArrayList<>());
    }

    public SearchResponse(long count) {
        this(count, new ArrayList<>());
    }

    public SearchResponse(long count, List<SearchData> dataList) {
        this.count = count;
        this.dataList = dataList;
        this.result = (count > 0);
    }

    public void addData(SearchData data) {
        dataList.add(data);
    }

    @Override
    public String toString() {
        return "SearchResponse{" +
                "result=" + result +
                ", count=" + count +
                ", dataList=" + dataList +
                '}';
    }

    public boolean getResult() {
        return result;
    }

    public long getCount() {
        return count;
    }

    public List<SearchData> getDataList() {
        return dataList;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void cutDataList(int limit, int offset) {
        if (limit > 0 && !dataList.isEmpty()) {
            if (offset > 0) {
                dataList = dataList.stream()
                        .skip(offset)
                        .limit(limit)
                        .toList();
            } else {
                dataList = dataList.stream()
                        .limit(limit)
                        .toList();
            }
        }
    }

    @JsonIgnore
    public static SearchResponse getEmpty() {
        return EMPTY_RESPONSE;
    }
}
