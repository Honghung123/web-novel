package com.group17.comic.plugins.crawler.concretes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.group17.comic.dtos.request.AlternatedChapterRequest;
import com.group17.comic.dtos.response.AuthorResponse;
import com.group17.comic.enums.ExceptionType;
import com.group17.comic.exceptions.BusinessException;
import com.group17.comic.log.Logger;
import com.group17.comic.models.*;
import com.group17.comic.plugins.crawler.IDataCrawler;
import com.group17.comic.plugins.crawler.WebCrawler;
import com.group17.comic.utils.*;

import lombok.SneakyThrows;

public class TruyenFullCrawler extends WebCrawler implements IDataCrawler {
    private static final String COMIC_API_URL = "https://api.truyenfull.vn/";
    private static final String COMIC_BASE_URL = "https://truyenfull.vision/";
    private static final UUID PLUGIN_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614173002");
    private static final String ONLY_NUMBER_REGEX = "^\\d+$";

    @Override
    public UUID getID() {
        return PLUGIN_ID;
    }

    @Override
    public String getPluginName() {
        return "Truyen Full";
    }

    private Map<String, Integer> initialCategoryList() {
        Map<String, Integer> categories = new HashMap<>();
        var genreList = this.getGenres();
        int categoryId = 1;
        for (Genre genre : genreList) {
            categories.put(genre.getTag(), categoryId++);
        }
        return categories;
    }

    private Integer getCategoryId(String genre) {
        Map<String, Integer> categories = initialCategoryList();
        if (categories.containsKey(genre)) {
            return categories.get(genre);
        } else {
            throw new BusinessException(ExceptionType.INVALID_GENRE);
        }
    }

    @SneakyThrows
    @Override
    public SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> search(
            String keyword, String byGenre, int currentPage) {
        keyword = StringUtility.removeDiacriticalMarks(keyword);
        if (StringUtils.hasLength(keyword) && StringUtils.hasLength(byGenre)) {
            return searchByKeywordAndGenre(keyword, byGenre, currentPage);
        } else if (keyword.isEmpty() && StringUtils.hasLength(byGenre)) {
            return searchOnlyByGenre(byGenre, currentPage);
        } else if (StringUtils.hasLength(keyword) && byGenre.isEmpty()) {
            return searchOnlyByKeyword(keyword, currentPage);
        } else {
            return this.getHotOrPromoteComics(currentPage);
        }
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchByKeywordAndGenre(
            String keyword, String byGenre, int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        Integer categoryId = this.getCategoryId(byGenre);
        String apiUrl =
                COMIC_API_URL + "v1/tim-kiem?title=" + term + "&category=[" + categoryId + "]&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories =
                            jsonObj.get("categories").getAsString().split(",");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                .toLowerCase()
                                .replace(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                            .toLowerCase()
                            .replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = LatestComic.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(ALTERNATE_IMAGE)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchOnlyByGenre(
            String byGenre, int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String apiUrl = COMIC_API_URL + "/v1/story/cate?cate=" + byGenre + "&type=story_new&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories =
                            jsonObj.get("categories").getAsString().split(",");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                .toLowerCase()
                                .replace(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                            .toLowerCase()
                            .replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = LatestComic.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(ALTERNATE_IMAGE)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> searchOnlyByKeyword(
            String keyword, int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String term = keyword.trim().replace(" ", "%20");
        String apiUrl = COMIC_API_URL + "/v1/tim-kiem?title=" + term + "&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination;
        List<AuthorResponse> authorList = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String title = jsonObj.get("title").getAsString();
                    String formatedTitle =
                            StringUtility.removeDiacriticalMarks(title).toLowerCase();
                    String formatedKeyword =
                            StringUtility.removeDiacriticalMarks(keyword).toLowerCase();
                    if (formatedTitle.contains(formatedKeyword)) {
                        String comicTagId = jsonObj.get("id").getAsString();
                        String image = jsonObj.get("image").getAsString();
                        String[] categories =
                                jsonObj.get("categories").getAsString().split(",");
                        List<Genre> genres = new ArrayList<>();
                        for (String untrimedCategory : categories) {
                            String category = untrimedCategory.trim();
                            String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                    .toLowerCase()
                                    .replace(" ", "-");
                            genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                        }
                        String authorName = jsonObj.get("author").getAsString();
                        String authorId = StringUtility.removeDiacriticalMarks(authorName)
                                .toLowerCase()
                                .replace(" ", "-");
                        var author = new Author(authorId, authorName);
                        int newestChapter = jsonObj.get("total_chapters").getAsInt();
                        String updatedTime = jsonObj.get("time").getAsString();
                        boolean isFull = false;
                        var comicModel = LatestComic.builder()
                                .tagId(comicTagId)
                                .title(title)
                                .image(image)
                                .alternateImage(ALTERNATE_IMAGE)
                                .genres(genres)
                                .author(author)
                                .newestChapter(newestChapter)
                                .totalChapter(newestChapter)
                                .updatedTime(updatedTime)
                                .isFull(isFull)
                                .build();
                        listMatchedComic.add(comicModel);
                    } else {
                        String authorName = jsonObj.get("author").getAsString();
                        String authorId = StringUtility.removeDiacriticalMarks(authorName)
                                .toLowerCase()
                                .replace(" ", "-");
                        if (authorList.stream()
                                .noneMatch(author -> author.getAuthorId().equals(authorId))) {
                            String comicTagId = jsonObj.get("id").getAsString();
                            authorList.add(new AuthorResponse(authorId, authorName, comicTagId));
                        }
                    }
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new SearchingPageableData<>(pagination, listMatchedComic, authorList);
    }

    @SneakyThrows
    @Override
    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<>();
        // Covert html to Document instance
        Document doc = this.getDocumentInstanceFromUrl(COMIC_BASE_URL);
        Elements elements = doc.select(".nav.navbar-nav li:nth-child(2) ul.dropdown-menu li a");
        for (Element element : elements) {
            String url = element.attr("href");
            String fullTagWithRedundantSlash = url.substring(url.lastIndexOf("the-loai"));
            String fullTag = fullTagWithRedundantSlash.substring(0, fullTagWithRedundantSlash.lastIndexOf("/"));
            String tag = fullTag.substring(fullTag.lastIndexOf("/") + 1);
            String label = element.text();
            genres.add(new Genre(label, tag, fullTag));
        }
        return genres;
    }

    @SneakyThrows
    @Override
    public PageableData<Integer, List<LatestComic>> getLastedComics(int currentPage) {
        String apiUrl = COMIC_API_URL + "v1/story/all?type=story_update&page=" + currentPage;
        List<LatestComic> lastedComics = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    String comicTagId = element.getAsJsonObject().get("id").getAsString();
                    String title = element.getAsJsonObject().get("title").getAsString();
                    String image = element.getAsJsonObject().get("image").getAsString();
                    String authorName = element.getAsJsonObject().get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                            .toLowerCase()
                            .replace(" ", "-");
                    Author author = new Author(authorId, authorName);
                    List<Genre> genres = new ArrayList<>();
                    String[] categories = element.getAsJsonObject()
                            .get("categories")
                            .getAsString()
                            .split(",");
                    for (String category : categories) {
                        category = category.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                .toLowerCase()
                                .replace(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    int newestChapter =
                            element.getAsJsonObject().get("total_chapters").getAsInt();
                    String updatedTime = element.getAsJsonObject().get("time").getAsString();
                    boolean isFull = element.getAsJsonObject().get("is_full").getAsBoolean();
                    var comicModel = LatestComic.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(ALTERNATE_IMAGE)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    lastedComics.add(comicModel);
                }
                var paginationOjbect = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationOjbect.get("total").getAsInt();
                int perPage = paginationOjbect.get("per_page").getAsInt();
                int totalPages = paginationOjbect.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                throw new BusinessException(ExceptionType.GET_LASTED_COMIC_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new PageableData<>(pagination, lastedComics);
    }

    @SneakyThrows
    @Override
    public Comic getComicInfo(String comicTagId) {
        if (!comicTagId.matches(ONLY_NUMBER_REGEX)) {
            throw new BusinessException(ExceptionType.INVALID_COMIC_TAG_ID);
        }
        int _id = Integer.parseInt(comicTagId);
        String apiUrl = COMIC_API_URL + "v1/story/detail/" + _id;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Comic comic;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject =
                        new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                String[] genresArray =
                        jsonObject.get("categories").getAsString().split(",");
                String authorName = jsonObject.get("author").getAsString();
                String authorId = StringUtility.removeDiacriticalMarks(authorName)
                        .toLowerCase()
                        .replace(" ", "-");
                var genres = Arrays.stream(genresArray)
                        .map(genre -> {
                            String category = genre.trim();
                            String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                    .toLowerCase()
                                    .replace(" ", "-");
                            return new Genre(category, convertedCategory, "the-loai/" + convertedCategory);
                        })
                        .toList();
                comic = Comic.builder()
                        .tagId(jsonObject.get("id").getAsString())
                        .title(jsonObject.get("title").getAsString())
                        .image(jsonObject.get("image").getAsString())
                        .description(jsonObject.get("description").getAsString())
                        .author(new Author(authorId, authorName))
                        .genres(genres)
                        .alternateImage(ALTERNATE_IMAGE)
                        .build();
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return comic;
    }

    @SneakyThrows
    private SearchingPageableData<Integer, List<LatestComic>, List<AuthorResponse>> getHotOrPromoteComics(
            int currentPage) {
        List<LatestComic> listMatchedComic = new ArrayList<>();
        String apiUrl = COMIC_API_URL + "/v1/story/all?type=story_full_rate&page=" + currentPage;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        Pagination<Integer> pagination;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String[] categories =
                            jsonObj.get("categories").getAsString().split(",");
                    List<Genre> genres = new ArrayList<>();
                    for (String untrimedCategory : categories) {
                        String category = untrimedCategory.trim();
                        String convertedCategory = StringUtility.removeDiacriticalMarks(category)
                                .toLowerCase()
                                .replace(" ", "-");
                        genres.add(new Genre(category, convertedCategory, "the-loai/" + convertedCategory));
                    }
                    String authorName = jsonObj.get("author").getAsString();
                    String authorId = StringUtility.removeDiacriticalMarks(authorName)
                            .toLowerCase()
                            .replace(" ", "-");
                    var author = new Author(authorId, authorName);
                    int newestChapter = jsonObj.get("total_chapters").getAsInt();
                    String updatedTime = jsonObj.get("time").getAsString();
                    boolean isFull = false;
                    var comicModel = LatestComic.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(ALTERNATE_IMAGE)
                            .genres(genres)
                            .author(author)
                            .newestChapter(newestChapter)
                            .totalChapter(newestChapter)
                            .updatedTime(updatedTime)
                            .isFull(isFull)
                            .build();
                    listMatchedComic.add(comicModel);
                }
                var paginationObject = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObject.get("total").getAsInt();
                int perPage = paginationObject.get("per_page").getAsInt();
                int totalPages = paginationObject.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new SearchingPageableData<>(pagination, listMatchedComic, null);
    }

    @Override
    @SneakyThrows
    public PageableData<Integer, List<Chapter>> getChapters(String comicTagId, int currentPage) {
        if (!comicTagId.matches(ONLY_NUMBER_REGEX)) {
            throw new BusinessException(ExceptionType.INVALID_COMIC_TAG_ID);
        }
        int _id = Integer.parseInt(comicTagId);
        String apiUrl = COMIC_API_URL + "v1/story/detail/" + _id + "/chapters?page=" + currentPage;
        Pagination<Integer> pagination;
        List<Chapter> chapters = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                var paginationObj = jsonObject.getAsJsonObject("meta").getAsJsonObject("pagination");
                int totalItems = paginationObj.get("total").getAsInt();
                int perPage = paginationObj.get("per_page").getAsInt();
                int totalPages = paginationObj.get("total_pages").getAsInt();
                pagination = new Pagination<>(currentPage, perPage, totalPages, totalItems);
                PaginationUtility.updatePagination(pagination);
                JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    JsonObject chapterObject = element.getAsJsonObject();
                    String chapterNo = chapterObject.get("id").getAsString();
                    String title = chapterObject.get("title").getAsString();
                    int chapterNumber = StringUtility.extractChapterNoFromString(title);
                    if (title.contains(":")) {
                        title = title.substring(title.indexOf(":") + 1).trim();
                        if (title.contains(":")) {
                            title = title.substring(title.indexOf(":") + 1).trim();
                        }
                    }
                    chapters.add(new Chapter(chapterNumber, chapterNo, title));
                }
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_CONTENT_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new PageableData<>(pagination, chapters);
    }

    @Override
    @SneakyThrows
    public Comic getComicInfoOnOtherServer(AlternatedChapterRequest altChapterDto) {
        String tagId = this.getTagIdComicFromTitleAndAuthor(
                altChapterDto.title(), altChapterDto.authorName(), altChapterDto.comicTagId());
        return this.getComicInfo(tagId);
    }

    private String getTagIdComicFromTitleAndAuthor(String _title, String _authorName, String _comicTagId) {
        String keyword = _title;
        Logger.logInfo("Comic tag id: " + _comicTagId);
        keyword = StringUtility.removeDiacriticalMarks(keyword)
                .toLowerCase()
                .replace("[dich]", "")
                .replace("- suu tam", "");
        if (keyword.contains("-")) {
            keyword = keyword.substring(0, keyword.lastIndexOf("-")).trim();
        }
        String term = keyword.replace(" ", "%20");
        var formattedAuthor =
                StringUtility.removeDiacriticalMarks(_authorName).toLowerCase().trim();
        String apiUrl = COMIC_API_URL + "/v1/tim-kiem?title=" + term;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        String tagId = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String title = jsonObj.get("title").getAsString();
                    String formatedTitle =
                            StringUtility.removeDiacriticalMarks(title).toLowerCase();
                    if (StringUtility.findLongestCommonSubstring(formatedTitle, keyword)
                                    .length()
                            >= 0.5 * keyword.length()) {
                        String authorName = jsonObj.get("author").getAsString();
                        String authorFormattedName = StringUtility.removeDiacriticalMarks(authorName)
                                .toLowerCase()
                                .trim();
                        if (authorFormattedName.equals(formattedAuthor)) {
                            tagId = jsonObj.get("id").getAsString();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        if (tagId.isEmpty()) {
            throw new BusinessException(ExceptionType.COMIC_NOT_FOUND);
        }
        return tagId;
    }

    @Override
    @SneakyThrows
    public PageableData<Integer, ComicChapterContent> getComicChapterContent(String comicTagId, String currentChapter) {
        if (!comicTagId.matches(ONLY_NUMBER_REGEX)) {
            throw new BusinessException(ExceptionType.INVALID_COMIC_TAG_ID);
        }
        Integer chapterId = Integer.parseInt(currentChapter);
        String apiUrl = COMIC_API_URL + "v1/chapter/detail/" + currentChapter;
        Pagination<Integer> pagination;
        ComicChapterContent chapterContent;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject =
                        new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                pagination = new Pagination<>(chapterId, 1, 1, 1);
                if (!jsonObject.get("chapter_prev").isJsonNull()) {
                    pagination.setPreviousPage(jsonObject.get("chapter_prev").getAsInt());
                }
                if (!jsonObject.get("chapter_next").isJsonNull()) {
                    pagination.setNextPage(jsonObject.get("chapter_next").getAsInt());
                }
                String title = jsonObject.get("story_name").getAsString();
                String chapterTitle = jsonObject.get("chapter_name").getAsString();
                int chapterNumber = StringUtility.extractChapterNoFromString(chapterTitle);
                if (chapterTitle.contains(":")) {
                    chapterTitle = chapterTitle
                            .substring(chapterTitle.indexOf(":") + 1)
                            .trim();
                    if (chapterTitle.contains(":")) {
                        chapterTitle = chapterTitle
                                .substring(chapterTitle.indexOf(":") + 1)
                                .trim();
                    }
                }
                String content = jsonObject.get("content").getAsString();
                Author author = this.getAuthorOfComic(comicTagId);
                chapterContent =
                        new ComicChapterContent(title, chapterTitle, content, comicTagId, author, chapterNumber);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_CONTENT_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
        return new PageableData<>(pagination, chapterContent);
    }

    @SneakyThrows
    private Author getAuthorOfComic(String comicTagId) {
        String apiUrl = COMIC_API_URL + "v1/story/detail/" + comicTagId;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject =
                        new Gson().fromJson(responseBody, JsonObject.class).getAsJsonObject("data");
                String authorName = jsonObject.get("author").getAsString();
                String authorId = StringUtility.removeDiacriticalMarks(authorName)
                        .toLowerCase()
                        .replace(" ", "-");
                return new Author(authorId, authorName);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
    }

    @Override
    @SneakyThrows
    public PageableData<?, ComicChapterContent> getComicChapterContentOnOtherServer(
            AlternatedChapterRequest altChapterDto) {
        String tagId = this.getTagIdComicFromTitleAndAuthor(
                altChapterDto.title(), altChapterDto.authorName(), altChapterDto.comicTagId());
        String chapterUrl = "";
        int currentPage = 1;
        while (true) {
            PageableData<Integer, List<Chapter>> result = this.getChapters(tagId, currentPage);
            List<Chapter> chapters = result.getData();
            if (chapters == null) {
                throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_LIST_FAILED);
            }
            if (chapters.isEmpty()) {
                throw new BusinessException(ExceptionType.GET_COMIC_CHAPTER_LIST_FAILED);
            }
            for (Chapter chapter : chapters) {
                if (chapter.getChapterNumber() == altChapterDto.chapterNumber()) {
                    chapterUrl = chapter.getChapterNo();
                    break;
                }
            }
            if (!chapterUrl.isEmpty()) {
                break;
            }
            currentPage++;
        }
        return this.getComicChapterContent(tagId, chapterUrl);
    }

    @Override
    @SneakyThrows
    public PageableData<Integer, List<LatestComic>> getComicsByAuthor(String authorId, String tagId, int currentPage) {
        List<LatestComic> authorComics = new ArrayList<>();
        String apiUrl = COMIC_API_URL + "/v1/story/detail/" + tagId + "/story_author";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray jsonArray = jsonObject.getAsJsonArray("data");
                for (JsonElement element : jsonArray) {
                    var jsonObj = element.getAsJsonObject();
                    String comicTagId = jsonObj.get("id").getAsString();
                    String title = jsonObj.get("title").getAsString();
                    String image = jsonObj.get("image").getAsString();
                    String authorName = jsonObj.get("author").getAsString();
                    var author = new Author(authorId, authorName);
                    List<Genre> genres = new ArrayList<>();
                    boolean isFull = false;
                    var comicModel = LatestComic.builder()
                            .tagId(comicTagId)
                            .title(title)
                            .image(image)
                            .alternateImage(ALTERNATE_IMAGE)
                            .genres(genres)
                            .author(author)
                            .isFull(isFull)
                            .build();
                    authorComics.add(comicModel);
                }
                var jsonPagination = jsonObject
                        .get("meta")
                        .getAsJsonObject()
                        .get("pagination")
                        .getAsJsonObject();
                int perPage = jsonPagination.get("per_page").getAsInt();
                int totalPages = jsonPagination.get("total_pages").getAsInt();
                Pagination<Integer> pagination = new Pagination<>(currentPage, perPage, totalPages, -1);
                return new PageableData<>(pagination, authorComics);
            } else {
                throw new BusinessException(ExceptionType.GET_COMIC_INFO_FAILED);
            }
        } catch (Exception e) {
            throw new BusinessException(ExceptionType.REQUEST_SERVER_TO_CRAWL_FAILED);
        }
    }
}
