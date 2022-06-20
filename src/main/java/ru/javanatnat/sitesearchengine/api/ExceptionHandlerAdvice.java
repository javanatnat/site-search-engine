package ru.javanatnat.sitesearchengine.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.javanatnat.sitesearchengine.service.index.*;
import ru.javanatnat.sitesearchengine.service.search.*;
import ru.javanatnat.sitesearchengine.service.statistics.*;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private static final String INDEX_SITE_EXCEPTION_MSG = "Ошибка индексации";
    private static final String INDEX_REPEAT_START_EXCEPTION_MSG = "Индексация уже запущена";
    private static final String INDEX_STOP_ERROR_EXCEPTION_MSG = "Индексация не запущена";
    private static final String PAGE_OUT_OF_BOUNDS_EXCEPTION_MSG
            = "Данная страница находится за пределами сайтов указанных в конфигурационном файле";
    private static final String GET_STAT_EXCEPTION_MSG = "Ошибка получения данных";

    @ResponseBody
    @ExceptionHandler(IndexSiteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleIndexSiteException(IndexSiteException exc) {
        LOG.error("handleIndexSiteException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(INDEX_SITE_EXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(IndexRepeatStartException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleIndexRepeatStartException(IndexRepeatStartException exc) {
        LOG.error("handleIndexRepeatStartException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(INDEX_REPEAT_START_EXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(IndexStopErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleIndexStopErrorException(IndexStopErrorException exc) {
        LOG.error("handleIndexStopErrorException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(INDEX_STOP_ERROR_EXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(PageOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handlePageOutOfBoundsException(PageOutOfBoundsException exc) {
        LOG.error("handlePageOutOfBoundsException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(PAGE_OUT_OF_BOUNDS_EXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(SearchSiteException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleSearchSiteException(SearchSiteException exc) {
        LOG.error("handleSearchSiteException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(exc.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(GetStatException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleGetStatException(GetStatException exc) {
        LOG.error("handleGetStatException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(GET_STAT_EXCEPTION_MSG);
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleBodyResponse handleRuntimeException(RuntimeException exc) {
        LOG.error("handleRuntimeException: {}", exc.getMessage());
        return SimpleBodyResponse.getError(INDEX_SITE_EXCEPTION_MSG);
    }
}
