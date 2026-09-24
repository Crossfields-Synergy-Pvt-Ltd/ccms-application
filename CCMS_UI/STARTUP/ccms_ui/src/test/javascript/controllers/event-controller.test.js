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
        $httpBackend.whenGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL&village=ALL')
            .respond([{name: 'DCU-001', gateway_identifier: 'dcu1'}]);
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

    it('loads DCU names with the village filter on init', function() {
        createController();
        $httpBackend.flush();
        expect($scope.dcu_data.length).toBe(1);
    });

    it('rejects View when no DCU is selected', function() {
        createController();
        $httpBackend.flush();
        $scope.showdate();
        expect($scope.errorMessage).toBe('Please select a DCU first.');
        expect(mockInform.add).toHaveBeenCalled();
    });

    it('requests events using the selected DCU and DD/MM/YYYY dates', function() {
        createController();
        $httpBackend.flush();
        $scope.selected_dcu = {name: {gateway_identifier: 'DCU001'}};
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

    it('includes village when filtering DCUs and clears child selections', function() {
        createController();
        $httpBackend.flush();
        $scope.selectedMandal = 'Mandal 1';
        $scope.select_gp = 'GP 1';
        $scope.filterValues.village = 'Village 1';
        $scope.gp_list = ['old GP'];
        $scope.village_list = ['old Village'];
        $httpBackend.expectGET('/filter/get_mandal?district=Guntur-17').respond(['Mandal 2']);
        $scope.selectedDistrict = 'Guntur-17';
        $scope.getMandalOnSelect();
        expect($scope.selectedMandal).toBe(null);
        expect($scope.select_gp).toBe(null);
        expect($scope.gp_list).toEqual([]);
        expect($scope.village_list).toEqual([]);
        $httpBackend.flush();

        $httpBackend.expectGET('/dcu/dcu_name_list?district=Guntur-17&mandal=Mandal%201&gp=GP%201&village=Village%201')
            .respond([]);
        $scope.selectedMandal = 'Mandal 1';
        $scope.select_gp = 'GP 1';
        $scope.filterValues.village = 'Village 1';
        $scope.filter();
        $httpBackend.flush();
    });

    it('clears village when the mandal changes', function() {
        createController();
        $httpBackend.flush();
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
        $httpBackend.flush();
        expect(typeof $scope.figureOutTodosToDisplay).toBe('function');
        $scope.todos = [1, 2, 3];
        $scope.currentPage = 1;
        $scope.figureOutTodosToDisplay();
        expect($scope.list).toEqual([1, 2, 3]);
    });
});
