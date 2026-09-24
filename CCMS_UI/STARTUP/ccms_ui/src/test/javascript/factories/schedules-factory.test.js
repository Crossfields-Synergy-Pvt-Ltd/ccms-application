describe('schedulesFactory', function() {
    var schedulesFactory;
    var $httpBackend;

    beforeEach(module('schedulesFactory'));

    beforeEach(inject(function(_schedulesFactory_, _$httpBackend_) {
        schedulesFactory = _schedulesFactory_;
        $httpBackend = _$httpBackend_;
    }));

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('loads a schedule by id with a path separator', function() {
        $httpBackend.expectGET('/scheduler/list/42').respond({ scheduleId: 42 });
        schedulesFactory.getByID(42);
        $httpBackend.flush();
    });

    it('updates an existing schedule with PUT', function() {
        var schedule = { scheduleId: 42, schedules_name: 'night' };
        $httpBackend.expectPUT('/scheduler/update', schedule).respond({ code: 200 });
        schedulesFactory.update(schedule);
        $httpBackend.flush();
    });

    it('deletes a schedule and returns the response body', function() {
        var result;
        $httpBackend.expectDELETE('/scheduler/delete/42').respond({ code: 1 });
        schedulesFactory.delete(42).then(function(response) {
            result = response;
        });
        $httpBackend.flush();
        expect(result.code).toBe(1);
    });
});
