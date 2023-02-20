package app.tasks.constants;

public class QueryConstants {
    public static final String GET_TASK_WITH_ACCESS_SUBTASKS = """
            with
                tasks_shares as (
                    select t.*,
                        json_agg(json_build_object('userId',s.user_id,'accessType',s.access_type)) as shares
                    from Task as t
                    join sharing as s
                    on t.id = s.task_id
                        and s.user_id = :userId
                        and t.id = :taskId
                    group by id, deadline_date, description, is_done, is_starred, last_update_ts, priority, repeat_freq, tags, t.user_id
            ),
                sub_tasks as (
                    select t.id,
                        json_agg(json_build_object('subTaskId',st.id,'completed',
                            st.completed,'lastUpdateTs',st.last_update_ts, 'text',st.text)) as subtasks
                    from sub_task st
                    join task t on st.task_id = t.id
                    where st.task_id = :taskId
                    group by 1
                )
            select ts.*, coalesce(st.subtasks,'[]') as subtasks from
                tasks_shares ts
                left join sub_tasks st on ts.id = st.id
            """;

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

    public static final String GET_MAX_UPDATE_TS = """
            select max(last_update_ts)
            from task t
                join sharing s on t.id = s.task_id and s.user_id = :userId
            """;

}
