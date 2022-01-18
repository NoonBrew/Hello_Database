package movies;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databasePath;

    Database(String databasePath) { // constructor

        // create table or maure sure it is created.

        this.databasePath = databasePath;

        try (Connection connection = DriverManager.getConnection(databasePath);
        Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS movies " +
                    "(id integer primary key," +
                    "name text unique check (length (name) >= 1)," +
                    "stars INTEGER check (stars >=0 AND stars <=5)," +
                    "watched BOOLEAN)");

        } catch (SQLException e) {
            System.out.println("Error creating movie DB table because " + e);
        }

    }

    public void addNewMovie (Movie movie) {
        String insertSQL = "INSERT INTO movies (name, stars, watched) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(databasePath);
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, movie.getName());
            preparedStatement.setInt(2, movie.getStars());
            preparedStatement.setBoolean(3, movie.isWatched());
            preparedStatement.execute();

        } catch (SQLException e) {
            System.out.println("Error adding movie " + movie + " because " + e);
        }

    }

    public List<Movie> getAllMovies() {
        try (Connection connection = DriverManager.getConnection(databasePath);
        Statement statement = connection.createStatement()) {

            // get all movies.
            ResultSet movieResult = statement.executeQuery("SELECT * FROM movies ORDER BY name");

            List<Movie> movies = new ArrayList<>();

            while (movieResult.next()) {
                int id = movieResult.getInt("id");
                String name = movieResult.getString("name");
                int stars = movieResult.getInt("stars");
                boolean watched = movieResult.getBoolean("watched");

                Movie movie = new Movie(id, name, stars, watched);
                movies.add(movie);
            }
            return movies;

        } catch (SQLException e) {
            System.out.println("Error fetching all movies because " + e);
            return null;
        }
    }

    public List<Movie> getAllMoviesByWatched(boolean watchedStatus) {
        String selectByWatchedStatus = "SELECT * FROM movies WHERE watched = ?";
        try(Connection connection = DriverManager.getConnection(databasePath);
        PreparedStatement preparedStatement = connection.prepareStatement(selectByWatchedStatus)
        ) {

            preparedStatement.setBoolean(1, watchedStatus);
            ResultSet movieResults = preparedStatement.executeQuery();

            List<Movie> movies = new ArrayList<>();

            while (movieResults.next()) {
                int id = movieResults.getInt("id");
                String name = movieResults.getString("name");
                int stars = movieResults.getInt("stars");
                boolean watched = movieResults.getBoolean("watched");

                Movie movie = new Movie(id, name, stars, watched);

                movies.add(movie);
            }
            return movies;

        }catch (SQLException e) {
            System.out.println("Error getting movies because " + e);
            return null;
        }
    }

    public void updateMovie(Movie movie) {

         String updateSQL = "UPDATE movies SET stars = ?, watched = ? WHERE id = ?";
         try (Connection connection = DriverManager.getConnection(databasePath);
         PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {

             preparedStatement.setInt(1, movie.getStars());
             preparedStatement.setBoolean(2, movie.isWatched());
             preparedStatement.setInt(3, movie.getId());

             preparedStatement.execute();

         }catch (SQLException e) {
             System.out.println("Error updating movie: " + movie + " because " + e);
         }
    }

    public void deleteMovie(Movie movie) {
        String deleteSQL = "DELETE FROM movies WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(databasePath);
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            preparedStatement.setInt(1, movie.getId());
            preparedStatement.execute();

        } catch (SQLException e) {
            System.out.println("Error deleting movie " + movie + " because " + e);
        }
    }

    public List<Movie> searchMovie(String keyword) {
        String searchSQL = "SELECT * FROM movies WHERE upper(name) like upper(?)";
        try(Connection connection = DriverManager.getConnection(databasePath);
        PreparedStatement preparedStatement = connection.prepareStatement(searchSQL)) {

            preparedStatement.setString(1, "%" + keyword + "%");
            ResultSet searchedMovies = preparedStatement.executeQuery();

            List<Movie> foundMovies = new ArrayList<>();
            while (searchedMovies.next()) {
                int id = searchedMovies.getInt("id");
                String name = searchedMovies.getString("name");
                int stars = searchedMovies.getInt("stars");
                boolean watched = searchedMovies.getBoolean("watched");

                Movie movie = new Movie(id, name, stars, watched);
                foundMovies.add(movie);
            }
            return foundMovies;

        }catch (SQLException e) {
            System.out.println("Error searching for " + keyword + " because " + e);
            return null;
        }
    }
}
