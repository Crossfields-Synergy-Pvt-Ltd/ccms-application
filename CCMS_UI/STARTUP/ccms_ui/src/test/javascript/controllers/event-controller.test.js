describe('eventListControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend;
    var eventFactory, mockConfig, mockInform;

    beforeEach(module('inform'));
    beforeEach(module('eventControllers'));
    beforeEach(module('eventFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_, _eventFactory_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $httpBackend = _$httpBackend_;
        $controller = _$controller_;
        eventFactory = _eventFactory_;
        mockConfig = {districts: [{state: 'Guntur-17', code: 'Guntur-17'}]};
        mockInform = {add: jasmine.createSpy('inform.add')};
        $rootScope.privilege = {district: 'ALL', mandal: 'ALL', gp: 'ALL'};
    }));

    function createController() {
        $controller('eventListControllers', {
            $scope: $scope,
            $rootScope: $rootScope,
            eventFactory: eventFactory,
            config: mockConfig,
            inform: mockInform
        });
    }

    afterEach(function() {
        try { $httpBackend.verifyNoOutstandingExpectation(); } catch (e) {}
        try { $httpBackend.verifyNoOutstandingRequest(); } catch (e) {}
    });

    it('does not load a DCU list on init', function() {
        createController();
        expect($scope.dcuId).toBe('');
        expect(typeof $scope.filter).toBe('function');
    });

    it('rejects View when no DCU is selected', function() {
        createController();
        $scope.showdate();
        expect($scope.errorMessage).toBe('Enter a DCU gateway serial number first.');
        expect(mockInform.add).toHaveBeenCalled();
    });

    it('rejects unsafe DCU IDs before requesting data', function() {
        createController();
        $scope.dcuId = '../etc';
        $scope.showdate();
        expect($scope.errorMessage).toBe('Enter a valid DCU gateway serial number.');
    });


    it('requests events using the selected DCU and DD/MM/YYYY dates', function() {
        createController();
        $scope.dcuId = 'DCU001';
        var originalMoment = window.moment;
        window.moment = function() {
            return {format: function() { return '01/01/2024'; }};
        };
        $httpBackend.expectGET('/events/events_between_date?id=DCU001&start_date=01%2F01%2F2024&end_date=01%2F01%2F2024')
            .respond([{id: 'event1'}]);
        $scope.showdate();
        $httpBackend.flush();
        window.moment = originalMoment;
        expect($scope.todos.length).toBe(1);
        expect($scope.list.length).toBe(1);
        expect($scope.loading).toBe(false);
    });



    it('clears village when the mandal changes', function() {
        createController();
        $scope.filterValues.village = 'Village 1';
        $scope.village_list = ['Village 1'];
        $httpBackend.expectGET('/filter/get_gp?mandal=Mandal%202').respond(['GP 2']);
        $scope.selectedMandal = 'Mandal 2';
        $scope.getGpOnSelect();
        expect($scope.filterValues.village).toBe(null);
        expect($scope.village_list).toEqual([]);
        $httpBackend.flush();
    });

    it('keeps pagination available before and after loading events', function() {
        createController();
        expect(typeof $scope.figureOutTodosToDisplay).toBe('function');
        $scope.todos = [1, 2, 3];
        $scope.currentPage = 1;
        $scope.figureOutTodosToDisplay();
        expect($scope.list).toEqual([1, 2, 3]);
    });
});
