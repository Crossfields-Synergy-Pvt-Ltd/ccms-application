describe('operational hours controllers', function() {
    var $scope, $rootScope, $controller, $q;
    var operationalFactory, modifiedFactory;

    beforeEach(module('operationalControllers'));
    beforeEach(module('modified_operationalControllers'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$q_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
        $q = _$q_;
        operationalFactory = {
            getAllDcuNames: jasmine.createSpy('getAllDcuNames').and.returnValue($q.when({ data: [] })),
            getAllOperationalHourByDate: jasmine.createSpy('getAllOperationalHourByDate').and.returnValue($q.when({ data: [] })),
            getAllExport: jasmine.createSpy('getAllExport').and.returnValue($q.when({})),
            getByMandal: jasmine.createSpy('getByMandal').and.returnValue($q.when({ data: [] })),
            getByGp: jasmine.createSpy('getByGp').and.returnValue($q.when({ data: [] })),
            getByVillage: jasmine.createSpy('getByVillage').and.returnValue($q.when({ data: [] }))
        };
        modifiedFactory = {
            getAllDcuNames: jasmine.createSpy('getAllDcuNames').and.returnValue($q.when({ data: [] })),
            getAllByDate: jasmine.createSpy('getAllByDate').and.returnValue($q.when({ data: [] })),
            exportLightStatus: jasmine.createSpy('exportLightStatus').and.returnValue($q.when({}))
        };
    }));

    function createOperational() {
        $controller('operationalListControllers', {
            $scope: $scope, operationalFactory: operationalFactory,
            config: { districts: [] }
        });
        $rootScope.$digest();
    }

    it('does not request data without a selected DCU', function() {
        createOperational();
        $scope.showdate();
        expect(operationalFactory.getAllOperationalHourByDate).not.toHaveBeenCalled();
        expect($scope.errorMessage).toBe('Please select a DCU first.');
    });

    it('loads and paginates operational hours without redefining pagination', function() {
        createOperational();
        $scope.selected_dcu = { name: { gateway_identifier: 'DCU-1' } };
        var pagination = $scope.figureOutTodosToDisplay;
        $scope.showdate();
        $rootScope.$digest();
        expect(operationalFactory.getAllOperationalHourByDate).toHaveBeenCalled();
        expect($scope.figureOutTodosToDisplay).toBe(pagination);
    });

    it('does not replace table data during export', function() {
        createOperational();
        $scope.selected_dcu = { name: { gateway_identifier: 'DCU-1' } };
        $scope.list = [{ dcu_id: 'existing' }];
        $scope.export_operationalhour();
        $rootScope.$digest();
        expect($scope.list).toEqual([{ dcu_id: 'existing' }]);
    });

    it('uses the previous calendar month preset', function() {
        createOperational();
        expect($scope.opts.ranges.Today).toBeDefined();
        expect($scope.opts.ranges.Yesterday).toBeDefined();
        expect(Object.keys($scope.opts.ranges).length).toBe(5);
    });

    it('validates DCU selection on the modified page too', function() {
        $controller('modified_operationalListControllers', {
            $scope: $scope, modified_operationalFactory: modifiedFactory
        });
        $rootScope.$digest();
        $scope.showdate();
        expect(modifiedFactory.getAllByDate).not.toHaveBeenCalled();
        expect($scope.errorMessage).toBe('Please select a DCU first.');
    });
});
