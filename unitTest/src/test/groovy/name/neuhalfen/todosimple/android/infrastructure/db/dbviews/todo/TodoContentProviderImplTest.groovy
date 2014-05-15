package name.neuhalfen.todosimple.android.infrastructure.db.dbviews.todo

import android.content.UriMatcher
import android.net.Uri
import pl.polidea.robospock.RoboSpecification

import static name.neuhalfen.todosimple.helper.TestConstants.taskId1

class TodoContentProviderImplTest
        extends RoboSpecification {

    def "the UriMatcher correctly identifies URIs with the aggregate UUID as id"() {
        given:
        UriMatcher sut = TodoContentProviderImpl.sURIMatcher;

        when:
        Uri aggregateIdURI = TodoContentProvider.Factory.forAggregateId(taskId1);

        then:
        sut.match(aggregateIdURI) == TodoContentProviderImpl.TODO_AGGREGATE_ID
    }

    def "the UriMatcher correctly identifies URIs with the database pk (_id:long) as id"() {
        given:
        UriMatcher sut = TodoContentProviderImpl.sURIMatcher;

        when:
        Uri aggregateIdURI = TodoContentProvider.Factory.forContenProvider_Id(666);

        then:
        sut.match(aggregateIdURI) == TodoContentProviderImpl.TODO_ID
    }

}
