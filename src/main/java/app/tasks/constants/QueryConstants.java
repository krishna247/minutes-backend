package app.tasks.constants;

public class QueryConstants {
    public static String GET_TASKS_WITH_ACCESS = """
                select *
                from Task as t
                join sharing as s
                on  t.id = s.task_id and t.user_id = :userId
            """;
    public static String GET_TASK_WITH_ACCESS = """
                select *
                from Task as t
                join sharing as s
                on  t.id = s.task_id
                where t.id = :taskId and t.userId = :userId
            """;
//    public static String DELETE_TASK = """
//                DELETE FROM task
//                WHERE t.id in (:taskIds)
//            """;

}
