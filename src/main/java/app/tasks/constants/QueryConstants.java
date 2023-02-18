package app.tasks.constants;

public class QueryConstants {
    public static final String GET_TASK_WITH_ACCESS_JSON = """
            select t.*,
            json_agg(json_build_object('userId',s.user_id,'accessType',s.access_type)) as shares
            from Task as t
            join sharing as s
            on t.id = s.task_id and t.id= :taskId
                and s.user_id = :userId
            group by id, deadline_date, description, is_done, is_starred, last_update_ts, priority, repeat_freq, tags, t.user_id
            """; //TODO Add subtasks

    public static final String GET_TASKS_WITH_ACCESS_JSON = """
            select t.*,
            json_agg(json_build_object('userId',s.user_id,'accessType',s.access_type)) as shares
            from Task as t
            join sharing as s
            on t.id = s.task_id
                and s.user_id = :userId
            group by id, deadline_date, description, is_done, is_starred, last_update_ts, priority, repeat_freq, tags, t.user_id
            """;

    public static final String GET_SUBTASKS_WITH_ACCESS = """
            select st.*
            from sub_task st
            join task t on st.task_id = t.id
            join sharing s on s.task_id = t.id
            where
            st.id in (:subTaskIds)
            and s.access_type in ('edit','own')
            and t.user_id = :userId
            """;

}
